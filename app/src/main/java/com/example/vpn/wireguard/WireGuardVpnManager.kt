package com.example.vpn.wireguard

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.VpnService
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.VpnConnectionState
import com.example.vpn.model.VpnTunnelStats
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Statistics
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import com.wireguard.config.InetEndpoint
import com.wireguard.config.InetNetwork
import com.wireguard.config.Interface
import com.wireguard.config.Peer
import com.wireguard.crypto.Key
import com.wireguard.crypto.KeyPair
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
import java.net.InetAddress

/**
 * Manages the native WireGuard backend, VpnService lifecycle, network callbacks,
 * configuration assembly with leak prevention, and statistics polling.
 */
class WireGuardVpnManager(
    private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val backend: Backend by lazy { GoBackend(context.applicationContext) }
    private val tunnel = WireGuardTunnel("wireguard_wg0")

    private val _connectionState = MutableStateFlow(VpnConnectionState.DISCONNECTED)
    val connectionState: StateFlow<VpnConnectionState> = _connectionState.asStateFlow()

    private val _stats = MutableStateFlow(VpnTunnelStats())
    val stats: StateFlow<VpnTunnelStats> = _stats.asStateFlow()

    private val _lastErrorMessage = MutableStateFlow<String?>(null)
    val lastErrorMessage: StateFlow<String?> = _lastErrorMessage.asStateFlow()

    // Generated cryptographic client keypair stored safely in memory for this session
    private var clientKeyPair: KeyPair = KeyPair()
    private var currentConfig: Config? = null
    private var statsPollingJob: Job? = null
    private var sessionStartTime: Long = 0L

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        // Observe internal tunnel state transitions
        scope.launch {
            tunnel.state.collect { nativeState ->
                when (nativeState) {
                    Tunnel.State.UP -> {
                        _connectionState.value = VpnConnectionState.CONNECTED
                        sessionStartTime = System.currentTimeMillis()
                        startStatsPolling()
                    }
                    Tunnel.State.DOWN -> {
                        stopStatsPolling()
                        if (_connectionState.value == VpnConnectionState.DISCONNECTING ||
                            _connectionState.value == VpnConnectionState.CONNECTED
                        ) {
                            _connectionState.value = VpnConnectionState.DISCONNECTED
                        }
                    }
                    Tunnel.State.TOGGLE -> {
                        // Handled internally by backend
                    }
                }
            }
        }
        registerNetworkMonitoring()
    }

    /**
     * Checks if Android OS requires user prompt for VpnService permission.
     * Returns an Intent to launch if prompt is needed, or null if already authorized.
     */
    fun prepareVpn(): Intent? {
        return try {
            VpnService.prepare(context)
        } catch (e: Exception) {
            _lastErrorMessage.value = "Failed to prepare VpnService: ${e.message}"
            null
        }
    }

    /**
     * Retrieves or generates a fresh Curve25519 client public key to send to FastAPI backend.
     */
    fun getOrCreateClientPublicKey(): String {
        return clientKeyPair.publicKey.toBase64()
    }

    /**
     * Generates a fresh client keypair for maximum forward secrecy.
     */
    fun rotateClientKeyPair() {
        clientKeyPair = KeyPair()
    }

    /**
     * Builds a leak-proof WireGuard Config:
     * - IPv4 AllowedIP: 0.0.0.0/0
     * - IPv6 AllowedIP: ::/0 (prevents IPv6 bypass leak)
     * - Dedicated secure DNS servers (prevents DNS queries escaping to ISP)
     * - MTU clamped to prevent packet fragmentation
     * - Persistent Keepalive to prevent NAT timeout
     */
    fun buildWireGuardConfig(peerConfig: PeerConfigResponse): Config {
        val interfaceBuilder = Interface.Builder()
            .parsePrivateKey(clientKeyPair.privateKey.toBase64())
            .addAddress(InetNetwork.parse(peerConfig.assignedIpv4))

        // Add IPv6 address if assigned for complete dual-stack leak-proofing
        peerConfig.assignedIpv6?.let { ipv6 ->
            if (ipv6.isNotBlank()) {
                try {
                    interfaceBuilder.addAddress(InetNetwork.parse(ipv6))
                } catch (_: Exception) {
                    // Fallback gracefully if IPv6 format has quirks
                }
            }
        }

        // Add DNS servers to route all queries strictly through the encrypted tunnel
        peerConfig.dnsServers.forEach { dns ->
            try {
                interfaceBuilder.addDnsServer(InetAddress.getByName(dns))
            } catch (_: Exception) {
                // Ignore invalid DNS strings
            }
        }

        interfaceBuilder.setMtu(peerConfig.mtu.coerceIn(1280, 1500))

        val peerBuilder = Peer.Builder()
            .parsePublicKey(peerConfig.serverPublicKey)
            .setEndpoint(InetEndpoint.parse(peerConfig.endpoint))
            // Route all IPv4 and IPv6 traffic into the tunnel: Zero Leak
            .addAllowedIp(InetNetwork.parse("0.0.0.0/0"))
            .addAllowedIp(InetNetwork.parse("::/0"))
            .setPersistentKeepalive(peerConfig.keepaliveSeconds)

        peerConfig.presharedKey?.let { psk ->
            if (psk.isNotBlank()) {
                peerBuilder.parsePreSharedKey(psk)
            }
        }

        return Config.Builder()
            .setInterface(interfaceBuilder.build())
            .addPeer(peerBuilder.build())
            .build()
    }

    /**
     * Starts the WireGuard tunnel using the official GoBackend.
     * Transitions state to CONNECTING, and will reach CONNECTED once the native tunnel is UP.
     */
    suspend fun startTunnel(config: PeerConfigResponse): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _lastErrorMessage.value = null
            _connectionState.value = VpnConnectionState.CONNECTING

            val wireGuardConfig = buildWireGuardConfig(config)
            currentConfig = wireGuardConfig

            // Start native WireGuard tunnel
            backend.setState(tunnel, Tunnel.State.UP, wireGuardConfig)
            Result.success(Unit)
        } catch (e: Exception) {
            _connectionState.value = VpnConnectionState.ERROR
            _lastErrorMessage.value = e.message ?: "Failed to start WireGuard tunnel"
            Result.failure(e)
        }
    }

    /**
     * Stops the WireGuard tunnel and releases all native handles.
     */
    suspend fun stopTunnel(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _connectionState.value = VpnConnectionState.DISCONNECTING
            stopStatsPolling()
            backend.setState(tunnel, Tunnel.State.DOWN, null)
            currentConfig = null
            _connectionState.value = VpnConnectionState.DISCONNECTED
            Result.success(Unit)
        } catch (e: Exception) {
            _lastErrorMessage.value = e.message ?: "Failed to stop WireGuard tunnel"
            _connectionState.value = VpnConnectionState.DISCONNECTED
            Result.failure(e)
        }
    }

    /**
     * Polls actual statistics directly from GoBackend while connected.
     */
    private fun startStatsPolling() {
        stopStatsPolling()
        statsPollingJob = scope.launch {
            var lastRx = 0L
            var lastTx = 0L
            var lastTime = System.currentTimeMillis()

            while (isActive && _connectionState.value == VpnConnectionState.CONNECTED) {
                try {
                    val statistics: Statistics = backend.getStatistics(tunnel)
                    val currentRx = statistics.totalRx()
                    val currentTx = statistics.totalTx()
                    val currentTime = System.currentTimeMillis()

                    val timeDeltaSec = ((currentTime - lastTime) / 1000.0).coerceAtLeast(1.0)
                    val rxSpeed = ((currentRx - lastRx) / timeDeltaSec).toLong().coerceAtLeast(0L)
                    val txSpeed = ((currentTx - lastTx) / timeDeltaSec).toLong().coerceAtLeast(0L)

                    val duration = (currentTime - sessionStartTime) / 1000L

                    _stats.value = VpnTunnelStats(
                        rxBytes = currentRx,
                        txBytes = currentTx,
                        rxSpeedBps = rxSpeed,
                        txSpeedBps = txSpeed,
                        sessionDurationSeconds = duration,
                        lastHandshakeTime = currentTime
                    )

                    lastRx = currentRx
                    lastTx = currentTx
                    lastTime = currentTime
                } catch (_: Exception) {
                    // Statistics might be momentarily unavailable during handshakes
                }
                delay(1000)
            }
        }
    }

    private fun stopStatsPolling() {
        statsPollingJob?.cancel()
        statsPollingJob = null
    }

    /**
     * Monitors network transitions (e.g. Wi-Fi <-> Cellular switches).
     * Prevents leaks and restores connection if network drops and recovers.
     */
    private fun registerNetworkMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (_connectionState.value == VpnConnectionState.RECONNECTING) {
                    currentConfig?.let { cfg ->
                        scope.launch {
                            try {
                                backend.setState(tunnel, Tunnel.State.UP, cfg)
                            } catch (_: Exception) {
                                _connectionState.value = VpnConnectionState.ERROR
                            }
                        }
                    }
                }
            }

            override fun onLost(network: Network) {
                if (_connectionState.value == VpnConnectionState.CONNECTED) {
                    // Underlying network disconnected, transition to RECONNECTING without leaking
                    _connectionState.value = VpnConnectionState.RECONNECTING
                }
            }
        }

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
        } catch (_: Exception) {
            // Best effort network monitoring
        }
    }

    fun cleanup() {
        stopStatsPolling()
        try {
            networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        } catch (_: Exception) {}
    }
}
