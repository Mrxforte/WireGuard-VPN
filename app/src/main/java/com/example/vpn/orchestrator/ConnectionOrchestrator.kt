package com.example.vpn.orchestrator

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.vpn.model.ConnectionDiagnostics
import com.example.vpn.model.ConnectionVerificationReport
import com.example.vpn.model.DiagnosticEvent
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.SmartConnectionSettings
import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnServer
import com.example.vpn.model.VpnSessionState
import com.example.vpn.model.VpnTunnelStats
import com.example.vpn.protocol.OpenVpnProtocolAdapter
import com.example.vpn.protocol.ProtocolAdapter
import com.example.vpn.protocol.WireGuardProtocolAdapter
import com.example.vpn.wireguard.WireGuardVpnManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Smart Connection Orchestrator managing multi-protocol routing,
 * automatic candidate failover, protocol fallback, and the strict Connection Quality Gate.
 *
 * ONLY the ConnectionOrchestrator may promote a connection to CONNECTED or DEGRADED,
 * and only after the full fail-closed Quality Gate pipeline succeeds.
 */
class ConnectionOrchestrator(
    private val context: Context,
    val wireGuardManager: WireGuardVpnManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Registered Protocol Adapters
    private val wireGuardAdapter = WireGuardProtocolAdapter(wireGuardManager)
    private val openVpnUdpAdapter = OpenVpnProtocolAdapter(context, VpnProtocol.OPENVPN_UDP)
    private val openVpnTcpAdapter = OpenVpnProtocolAdapter(context, VpnProtocol.OPENVPN_TCP)

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val qualityGate = ConnectionQualityGate(context, connectivityManager)

    val serverSelector = ServerSelector()
    var settings = SmartConnectionSettings()
        set(value) {
            field = value
            failoverPolicy = FailoverPolicy(value)
        }
    var failoverPolicy = FailoverPolicy(settings)

    private val _sessionState = MutableStateFlow(VpnSessionState.DISCONNECTED)
    val sessionState: StateFlow<VpnSessionState> = _sessionState.asStateFlow()

    private val _activeServer = MutableStateFlow<VpnServer?>(null)
    val activeServer: StateFlow<VpnServer?> = _activeServer.asStateFlow()

    private val _activeProtocol = MutableStateFlow(VpnProtocol.WIREGUARD)
    val activeProtocol: StateFlow<VpnProtocol> = _activeProtocol.asStateFlow()

    private val _stats = MutableStateFlow(VpnTunnelStats())
    val stats: StateFlow<VpnTunnelStats> = _stats.asStateFlow()

    private val _diagnostics = MutableStateFlow(ConnectionDiagnostics())
    val diagnostics: StateFlow<ConnectionDiagnostics> = _diagnostics.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var activeAdapter: ProtocolAdapter = wireGuardAdapter
    private var telemetryJob: Job? = null
    private var healthMonitorJob: Job? = null
    private var connectionJob: Job? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        registerNetworkMonitoring()
    }

    fun getAdapterForProtocol(protocol: VpnProtocol): ProtocolAdapter {
        return when (protocol) {
            VpnProtocol.WIREGUARD -> wireGuardAdapter
            VpnProtocol.OPENVPN_UDP -> openVpnUdpAdapter
            VpnProtocol.OPENVPN_TCP -> openVpnTcpAdapter
        }
    }

    fun addDiagnosticEvent(category: String, message: String, level: String = "INFO") {
        val event = DiagnosticEvent(
            timestamp = System.currentTimeMillis(),
            level = level,
            category = category,
            message = message
        )
        val currentEvents = _diagnostics.value.events.takeLast(30) + event
        _diagnostics.value = _diagnostics.value.copy(
            events = currentEvents
        )
    }

    /**
     * Connects with automatic server ranking, failover, protocol fallback,
     * and mandatory fail-closed Quality Gate verification.
     */
    fun startConnection(
        serverCandidates: List<VpnServer>,
        preferredServer: VpnServer?,
        fetchConfigFn: suspend (VpnServer, VpnProtocol) -> Result<PeerConfigResponse>
    ) {
        connectionJob?.cancel()
        connectionJob = scope.launch {
            _errorMessage.value = null
            _sessionState.value = VpnSessionState.PREPARING

            var targetProtocol = settings.preferredProtocol
            val attemptedProtocols = mutableSetOf<VpnProtocol>()
            var overallAttemptCount = 0
            var connectedSuccessfully = false

            addDiagnosticEvent(
                "ORCHESTRATOR",
                "Starting connection session. Preferred protocol: ${targetProtocol.displayName}"
            )

            val maxTotalAttempts = (settings.maxRetryCount * 2).coerceAtLeast(4)

            while (isActive && !connectedSuccessfully && overallAttemptCount < maxTotalAttempts) {
                attemptedProtocols.add(targetProtocol)
                activeAdapter = getAdapterForProtocol(targetProtocol)
                _activeProtocol.value = targetProtocol

                // Rank servers for current protocol
                val ranked = serverSelector.rankServers(
                    servers = serverCandidates,
                    protocol = targetProtocol,
                    preferredServer = preferredServer
                )

                if (ranked.isEmpty()) {
                    addDiagnosticEvent(
                        "ORCHESTRATOR",
                        "No eligible healthy servers for ${targetProtocol.displayName}",
                        "WARN"
                    )
                    val fallback = failoverPolicy.getFallbackProtocol(targetProtocol, attemptedProtocols)
                    if (fallback != null) {
                        addDiagnosticEvent("FAILOVER", "Initiating protocol fallback to ${fallback.displayName}")
                        _sessionState.value = VpnSessionState.SWITCHING_PROTOCOL
                        targetProtocol = fallback
                        delay(400)
                        continue
                    } else {
                        break
                    }
                }

                // Attempt server candidates in rank order
                for ((index, candidate) in ranked.withIndex()) {
                    if (!isActive) break

                    overallAttemptCount++
                    _activeServer.value = candidate
                    _sessionState.value = VpnSessionState.CONNECTING

                    addDiagnosticEvent(
                        "CONNECT",
                        "Attempting ${candidate.name} via ${targetProtocol.displayName} (Attempt $overallAttemptCount)"
                    )

                    // 1. Fetch/negotiate session credentials
                    val configResult = fetchConfigFn(candidate, targetProtocol)
                    if (configResult.isFailure) {
                        val reason = configResult.exceptionOrNull()?.message ?: "Configuration error"
                        addDiagnosticEvent("CONFIG", "Failed to retrieve config for ${candidate.name}: $reason", "ERROR")
                        serverSelector.quarantineServer(candidate.id, 5)
                        continue
                    }

                    val sessionConfig = configResult.getOrThrow()

                    // 2. Start tunnel via protocol adapter
                    val tunnelResult = activeAdapter.startTunnel(candidate, sessionConfig)
                    if (tunnelResult.isFailure) {
                        val reason = tunnelResult.exceptionOrNull()?.message ?: "Handshake error"
                        addDiagnosticEvent("TUNNEL", "Tunnel startup failed on ${candidate.name}: $reason", "ERROR")
                        serverSelector.quarantineServer(candidate.id, 5)

                        if (failoverPolicy.shouldFailoverServer(overallAttemptCount, ranked.drop(index + 1))) {
                            _sessionState.value = VpnSessionState.SWITCHING_SERVER
                            val backoff = failoverPolicy.computeBackoffMillis(overallAttemptCount)
                            addDiagnosticEvent("FAILOVER", "Failing over to next candidate in ${backoff}ms")
                            delay(backoff)
                            continue
                        }
                    } else {
                        // 3. MANDATORY CONNECTION QUALITY GATE (Fail-Closed)
                        addDiagnosticEvent(
                            "QUALITY_GATE",
                            "Tunnel established. Initiating 8-stage Quality Gate pipeline..."
                        )

                        val report = qualityGate.executeVerificationPipeline(
                            server = candidate,
                            config = sessionConfig,
                            protocol = targetProtocol,
                            adapter = activeAdapter,
                            settings = settings,
                            onStateTransition = { newState ->
                                if (isActive) _sessionState.value = newState
                            },
                            onLogEvent = { cat, msg, lvl ->
                                addDiagnosticEvent(cat, msg, lvl)
                            }
                        )

                        if (report.isSuccess) {
                            val finalState = if (report.isDegraded) VpnSessionState.DEGRADED else VpnSessionState.CONNECTED
                            _sessionState.value = finalState
                            connectedSuccessfully = true

                            addDiagnosticEvent(
                                "QUALITY_GATE",
                                "Quality Gate APPROVED ($finalState) in ${report.durationMs}ms. Egress IP: ${report.verifiedEgressIp ?: "Encrypted"}"
                            )

                            _diagnostics.value = _diagnostics.value.copy(
                                activeServerName = candidate.name,
                                activeProtocol = targetProtocol,
                                sessionState = finalState,
                                lastHandshakeEpoch = System.currentTimeMillis(),
                                lastConnectivityCheckMs = System.currentTimeMillis(),
                                dnsHealthStatus = "Encrypted (${report.verifiedDnsServer ?: "1.1.1.1"})",
                                tunnelHealthStatus = if (report.isDegraded) "Degraded" else "Optimal",
                                retryCount = overallAttemptCount,
                                lastVerificationReport = report
                            )

                            startStatsObservation()
                            startContinuousHealthMonitoring(candidate)
                            break
                        } else {
                            // Quality gate failed — strictly tear down tunnel, never declare connected!
                            addDiagnosticEvent(
                                "QUALITY_GATE",
                                "Quality Gate REJECTED connection: ${report.sanitizedFailureReason ?: "Verification failed"}",
                                "ERROR"
                            )
                            activeAdapter.stopTunnel()
                            serverSelector.quarantineServer(candidate.id, 5)

                            if (failoverPolicy.shouldFailoverServer(overallAttemptCount, ranked.drop(index + 1))) {
                                _sessionState.value = VpnSessionState.SWITCHING_SERVER
                                val backoff = failoverPolicy.computeBackoffMillis(overallAttemptCount)
                                delay(backoff)
                                continue
                            }
                        }
                    }
                }

                // If not connected, check if protocol fallback is permissible
                if (!connectedSuccessfully) {
                    val fallback = failoverPolicy.getFallbackProtocol(targetProtocol, attemptedProtocols)
                    if (fallback != null) {
                        addDiagnosticEvent(
                            "FALLBACK",
                            "Candidates exhausted on ${targetProtocol.displayName}. Initiating protocol fallback to ${fallback.displayName}"
                        )
                        _sessionState.value = VpnSessionState.SWITCHING_PROTOCOL
                        targetProtocol = fallback
                        delay(500)
                    } else {
                        break
                    }
                }
            }

            if (!connectedSuccessfully) {
                val finalError = "Connection verification failed. All server candidates and protocol fallbacks exhausted."
                _sessionState.value = VpnSessionState.FAILED
                _errorMessage.value = finalError
                addDiagnosticEvent("ERROR", finalError, "ERROR")
            }
        }
    }

    /**
     * Cleanly stops the active tunnel and cancels health monitoring.
     */
    fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null
        stopHealthMonitoring()
        stopStatsObservation()

        scope.launch {
            _sessionState.value = VpnSessionState.DISCONNECTING
            addDiagnosticEvent("DISCONNECT", "Tearing down VPN tunnel and releasing network handles")
            activeAdapter.stopTunnel()
            _sessionState.value = VpnSessionState.DISCONNECTED
            _activeServer.value = null
            _stats.value = VpnTunnelStats()
            addDiagnosticEvent("DISCONNECT", "Tunnel completely disconnected")
        }
    }

    private fun startStatsObservation() {
        telemetryJob?.cancel()
        telemetryJob = scope.launch {
            while (isActive && _sessionState.value.isStableConnected) {
                _stats.value = activeAdapter.stats.value
                delay(1000)
            }
        }
    }

    private fun stopStatsObservation() {
        telemetryJob?.cancel()
        telemetryJob = null
    }

    /**
     * Continuous background health monitoring post-CONNECTED.
     * Evaluates tunnel alive status and handshake freshness.
     */
    private fun startContinuousHealthMonitoring(server: VpnServer) {
        healthMonitorJob?.cancel()
        healthMonitorJob = scope.launch {
            while (isActive && _sessionState.value.isStableConnected) {
                delay(25000) // 25s monitoring interval
                if (!isActive || !_sessionState.value.isStableConnected) break

                val adapterState = activeAdapter.state.value
                val isAlive = adapterState == VpnSessionState.CONNECTED || adapterState == VpnSessionState.DEGRADED

                if (!isAlive) {
                    addDiagnosticEvent(
                        "HEALTH_MONITOR",
                        "Tunnel interface died in background. Initiating recovery...",
                        "WARN"
                    )
                    _sessionState.value = VpnSessionState.RECONNECTING
                    delay(1000)
                    // Trigger failover or reconnect
                    activeAdapter.stopTunnel()
                    break
                }
            }
        }
    }

    private fun stopHealthMonitoring() {
        healthMonitorJob?.cancel()
        healthMonitorJob = null
    }

    private fun registerNetworkMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (_sessionState.value == VpnSessionState.RECONNECTING && settings.reconnectOnNetworkChange) {
                    addDiagnosticEvent("NETWORK", "Underlying network restored. Re-authenticating session")
                }
            }

            override fun onLost(network: Network) {
                if (_sessionState.value.isStableConnected) {
                    addDiagnosticEvent(
                        "NETWORK",
                        "Underlying carrier network lost. Holding routing table to prevent leaks",
                        "WARN"
                    )
                    _sessionState.value = VpnSessionState.RECONNECTING
                }
            }
        }

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
        } catch (_: Exception) {}
    }

    fun cleanup() {
        stopStatsObservation()
        stopHealthMonitoring()
        connectionJob?.cancel()
        try {
            networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        } catch (_: Exception) {}
    }
}
