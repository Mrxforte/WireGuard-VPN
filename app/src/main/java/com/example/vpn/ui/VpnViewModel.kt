package com.example.vpn.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpn.data.VpnRepository
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.SecurityLeakStatus
import com.example.vpn.model.VpnConnectionState
import com.example.vpn.model.VpnServer
import com.example.vpn.model.VpnTunnelStats
import com.example.vpn.service.VpnActionReceiver
import com.example.vpn.service.VpnNotificationManager
import com.example.vpn.wireguard.WireGuardVpnManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ViewModel managing VPN connection state, FastAPI server communication,
 * peer key exchange, lease renewals, and telemetry updates.
 */
class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VpnRepository(application)
    private val vpnManager = WireGuardVpnManager(application)
    private val notificationManager = VpnNotificationManager(application)

    val connectionState: StateFlow<VpnConnectionState> = vpnManager.connectionState
    val stats: StateFlow<VpnTunnelStats> = vpnManager.stats
    val wireGuardError: StateFlow<String?> = vpnManager.lastErrorMessage

    val backendUrl: StateFlow<String> = repository.backendUrlFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, VpnRepository.DEFAULT_BACKEND_URL)

    val authToken: StateFlow<String?> = repository.authTokenFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val username: StateFlow<String> = repository.usernameFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "WireGuard User")

    private val _servers = MutableStateFlow<List<VpnServer>>(repository.getDefaultServers())
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    private val _selectedServer = MutableStateFlow<VpnServer>(repository.getDefaultServers().first())
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _currentPeerConfig = MutableStateFlow<PeerConfigResponse?>(null)
    val currentPeerConfig: StateFlow<PeerConfigResponse?> = _currentPeerConfig.asStateFlow()

    private val _securityStatus = MutableStateFlow(SecurityLeakStatus())
    val securityStatus: StateFlow<SecurityLeakStatus> = _securityStatus.asStateFlow()

    private val _vpnPermissionIntent = MutableStateFlow<Intent?>(null)
    val vpnPermissionIntent: StateFlow<Intent?> = _vpnPermissionIntent.asStateFlow()

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage.asStateFlow()

    private val _leaseRemainingSeconds = MutableStateFlow<Long>(0L)
    val leaseRemainingSeconds: StateFlow<Long> = _leaseRemainingSeconds.asStateFlow()

    private var leaseMonitorJob: Job? = null
    private var notificationSyncJob: Job? = null

    init {
        // Register receiver hook for notification Disconnect action
        VpnActionReceiver.onDisconnectRequested = {
            disconnect()
        }

        // Fetch server list
        loadServers()

        // Sync foreground notification with telemetry while connected
        notificationSyncJob = viewModelScope.launch {
            connectionState.collect { state ->
                when (state) {
                    VpnConnectionState.CONNECTED -> {
                        _uiErrorMessage.value = null
                        startNotificationSync()
                        startLeaseMonitoring()
                    }
                    VpnConnectionState.DISCONNECTED -> {
                        notificationManager.cancelNotification()
                        stopLeaseMonitoring()
                        _leaseRemainingSeconds.value = 0L
                    }
                    VpnConnectionState.CONNECTING -> {
                        notificationManager.showConnectingNotification(_selectedServer.value)
                    }
                    VpnConnectionState.ERROR -> {
                        notificationManager.cancelNotification()
                        stopLeaseMonitoring()
                    }
                    else -> Unit
                }
            }
        }
    }

    fun loadServers() {
        viewModelScope.launch {
            val list = repository.getServers()
            _servers.value = list
            if (list.none { it.id == _selectedServer.value.id }) {
                _selectedServer.value = list.firstOrNull() ?: repository.getDefaultServers().first()
            }
        }
    }

    fun selectServer(server: VpnServer) {
        if (connectionState.value == VpnConnectionState.CONNECTED) {
            // Disconnect before switching servers
            viewModelScope.launch {
                disconnect()
                _selectedServer.value = server
            }
        } else {
            _selectedServer.value = server
        }
    }

    /**
     * Entry point when user taps Connect button.
     * Evaluates VpnService preparation first.
     */
    fun startConnection() {
        _uiErrorMessage.value = null
        val prepareIntent = vpnManager.prepareVpn()
        if (prepareIntent != null) {
            // Prompt the user for VPN permission
            _vpnPermissionIntent.value = prepareIntent
        } else {
            // Already authorized, proceed directly
            proceedWithConnection()
        }
    }

    fun onVpnPermissionResult(isGranted: Boolean) {
        _vpnPermissionIntent.value = null
        if (isGranted) {
            proceedWithConnection()
        } else {
            _uiErrorMessage.value = "VPN permission denied by user. Android requires VPN authorization to establish a secure WireGuard tunnel."
        }
    }

    private fun proceedWithConnection() {
        viewModelScope.launch {
            try {
                _uiErrorMessage.value = null

                // 1. Authenticate with FastAPI if token is missing
                var token = authToken.value
                if (token.isNullOrBlank()) {
                    val authResult = repository.loginAsGuest()
                    if (authResult.isSuccess) {
                        token = authResult.getOrNull()?.accessToken
                    }
                }

                // 2. Generate/retrieve client public key
                val clientPublicKey = vpnManager.getOrCreateClientPublicKey()

                // 3. Negotiate dynamic peer configuration with FastAPI backend
                val peerResult = repository.registerPeer(_selectedServer.value, clientPublicKey)
                if (peerResult.isFailure) {
                    _uiErrorMessage.value = "Failed to obtain VPN configuration from backend: ${peerResult.exceptionOrNull()?.message}"
                    return@launch
                }

                val peerConfig = peerResult.getOrThrow()
                _currentPeerConfig.value = peerConfig

                // 4. Start the WireGuard tunnel with official GoBackend
                val tunnelResult = vpnManager.startTunnel(peerConfig)
                if (tunnelResult.isFailure) {
                    _uiErrorMessage.value = "WireGuard tunnel startup failed: ${tunnelResult.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _uiErrorMessage.value = "Connection error: ${e.message}"
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            _currentPeerConfig.value?.let { config ->
                repository.revokePeer(config.peerId)
            }
            vpnManager.stopTunnel()
            _currentPeerConfig.value = null
            notificationManager.cancelNotification()
        }
    }

    fun clearError() {
        _uiErrorMessage.value = null
    }

    fun updateBackendUrl(url: String) {
        viewModelScope.launch {
            repository.saveBackendUrl(url)
            loadServers()
        }
    }

    fun login(username: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(username, pass)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Authentication failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            if (connectionState.value == VpnConnectionState.CONNECTED) {
                disconnect()
            }
            repository.clearAuthToken()
        }
    }

    private fun startNotificationSync() {
        viewModelScope.launch {
            while (isActive && connectionState.value == VpnConnectionState.CONNECTED) {
                notificationManager.showConnectedNotification(_selectedServer.value, stats.value)
                delay(1000)
            }
        }
    }

    /**
     * Monitors configuration lease expiration and performs automated peer renewal.
     */
    private fun startLeaseMonitoring() {
        stopLeaseMonitoring()
        leaseMonitorJob = viewModelScope.launch {
            while (isActive && connectionState.value == VpnConnectionState.CONNECTED) {
                val config = _currentPeerConfig.value
                if (config != null) {
                    val currentEpoch = System.currentTimeMillis() / 1000
                    val remaining = config.expiresAtEpochSeconds - currentEpoch
                    _leaseRemainingSeconds.value = remaining.coerceAtLeast(0L)

                    // Auto-renew if less than 5 minutes remain
                    if (remaining in 1..300) {
                        val renewal = repository.renewPeer(config.peerId)
                        if (renewal.isSuccess) {
                            val newExpiry = renewal.getOrThrow()
                            _currentPeerConfig.value = config.copy(expiresAtEpochSeconds = newExpiry)
                        }
                    } else if (remaining <= 0) {
                        // Lease expired and peer revoked on server
                        _uiErrorMessage.value = "VPN configuration lease expired. Reconnecting..."
                        disconnect()
                        delay(1000)
                        startConnection()
                        break
                    }
                }
                delay(2000)
            }
        }
    }

    private fun stopLeaseMonitoring() {
        leaseMonitorJob?.cancel()
        leaseMonitorJob = null
    }

    override fun onCleared() {
        super.onCleared()
        notificationSyncJob?.cancel()
        stopLeaseMonitoring()
        vpnManager.cleanup()
        notificationManager.cancelNotification()
    }
}
