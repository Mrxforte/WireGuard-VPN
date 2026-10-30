package com.example.vpn.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vpn.data.VpnRepository
import com.example.vpn.model.ConnectionDiagnostics
import com.example.vpn.model.ConnectionVerificationReport
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.RegisteredDevice
import com.example.vpn.model.SecurityLeakStatus
import com.example.vpn.model.SmartConnectionSettings
import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnServer
import com.example.vpn.model.VpnSessionState
import com.example.vpn.model.VpnTunnelStats
import com.example.vpn.orchestrator.ConnectionOrchestrator
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
 * Enterprise ViewModel orchestrating multi-protocol VPN connections,
 * enforcing the fail-closed Connection Quality Gate, managing Django REST API
 * sessions, and syncing real-time diagnostics.
 */
class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VpnRepository(application)
    private val wireGuardManager = WireGuardVpnManager(application)
    private val notificationManager = VpnNotificationManager(application)

    val orchestrator = ConnectionOrchestrator(application, wireGuardManager)

    // Authoritative State Machine
    val connectionState: StateFlow<VpnSessionState> = orchestrator.sessionState
    val activeProtocol: StateFlow<VpnProtocol> = orchestrator.activeProtocol
    val stats: StateFlow<VpnTunnelStats> = orchestrator.stats
    val diagnostics: StateFlow<ConnectionDiagnostics> = orchestrator.diagnostics
    val activeServer: StateFlow<VpnServer?> = orchestrator.activeServer

    val smartSettings: StateFlow<SmartConnectionSettings> = repository.smartSettingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, SmartConnectionSettings())

    val backendUrl: StateFlow<String> = repository.backendUrlFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, VpnRepository.DEFAULT_DJANGO_URL)

    val authToken: StateFlow<String?> = repository.authTokenFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val username: StateFlow<String> = repository.usernameFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "SmartVPN User")

    val userTier: StateFlow<String> = repository.userTierFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Pro")

    private val _servers = MutableStateFlow<List<VpnServer>>(repository.getDefaultServers())
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    private val _selectedServer = MutableStateFlow<VpnServer>(repository.getDefaultServers().first())
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _vpnPermissionIntent = MutableStateFlow<Intent?>(null)
    val vpnPermissionIntent: StateFlow<Intent?> = _vpnPermissionIntent.asStateFlow()

    private val _uiErrorMessage = MutableStateFlow<String?>(null)
    val uiErrorMessage: StateFlow<String?> = _uiErrorMessage.asStateFlow()

    private val _registeredDevices = MutableStateFlow<List<RegisteredDevice>>(emptyList())
    val registeredDevices: StateFlow<List<RegisteredDevice>> = _registeredDevices.asStateFlow()

    private var notificationSyncJob: Job? = null

    init {
        // Register notification Disconnect hook
        VpnActionReceiver.onDisconnectRequested = {
            disconnect()
        }

        loadServers()
        loadDevices()

        // Sync repository settings to orchestrator
        viewModelScope.launch {
            smartSettings.collect { settings ->
                orchestrator.settings = settings
            }
        }

        // Sync errors from orchestrator
        viewModelScope.launch {
            orchestrator.errorMessage.collect { err ->
                if (err != null) {
                    _uiErrorMessage.value = err
                }
            }
        }

        // Notification management
        notificationSyncJob = viewModelScope.launch {
            connectionState.collect { state ->
                when {
                    state.isStableConnected -> {
                        _uiErrorMessage.value = null
                        startNotificationSync()
                    }
                    state == VpnSessionState.DISCONNECTED -> {
                        notificationManager.cancelNotification()
                    }
                    state == VpnSessionState.FAILED -> {
                        notificationManager.cancelNotification()
                    }
                    state.isProgressing -> {
                        notificationManager.showConnectingNotification(_selectedServer.value)
                    }
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

    fun loadDevices() {
        viewModelScope.launch {
            _registeredDevices.value = repository.getRegisteredDevices()
        }
    }

    fun selectServer(server: VpnServer) {
        if (connectionState.value.isStableConnected) {
            disconnect()
        }
        _selectedServer.value = server
    }

    fun selectProtocol(protocol: VpnProtocol) {
        viewModelScope.launch {
            val updated = smartSettings.value.copy(preferredProtocol = protocol)
            repository.updateSmartSettings(updated)
            orchestrator.settings = updated
        }
    }

    fun updateSettings(settings: SmartConnectionSettings) {
        viewModelScope.launch {
            repository.updateSmartSettings(settings)
            orchestrator.settings = settings
        }
    }

    fun toggleFavorite(serverId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(serverId)
            loadServers()
        }
    }

    fun revokeDevice(deviceId: String) {
        viewModelScope.launch {
            repository.revokeDevice(deviceId)
            loadDevices()
        }
    }

    /**
     * Entry point when user taps the main Connect button.
     */
    fun startConnection() {
        _uiErrorMessage.value = null
        val prepareIntent = wireGuardManager.prepareVpn()
        if (prepareIntent != null) {
            _vpnPermissionIntent.value = prepareIntent
        } else {
            proceedWithOrchestratedConnection()
        }
    }

    fun onVpnPermissionResult(isGranted: Boolean) {
        _vpnPermissionIntent.value = null
        if (isGranted) {
            proceedWithOrchestratedConnection()
        } else {
            _uiErrorMessage.value = "VPN permission denied by user. Android requires VPN authorization to establish an encrypted tunnel."
        }
    }

    private fun proceedWithOrchestratedConnection() {
        _uiErrorMessage.value = null
        val candidates = _servers.value
        val preferred = _selectedServer.value

        orchestrator.startConnection(
            serverCandidates = candidates,
            preferredServer = preferred,
            fetchConfigFn = { server, protocol ->
                val clientPublicKey = wireGuardManager.getOrCreateClientPublicKey()
                repository.registerPeerSession(server, protocol, clientPublicKey)
            }
        )
    }

    fun disconnect() {
        orchestrator.disconnect()
        notificationManager.cancelNotification()
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
                loadDevices()
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Authentication failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            if (connectionState.value.isStableConnected) {
                disconnect()
            }
            repository.clearAuthToken()
        }
    }

    private fun startNotificationSync() {
        viewModelScope.launch {
            while (isActive && connectionState.value.isStableConnected) {
                val srv = orchestrator.activeServer.value ?: _selectedServer.value
                notificationManager.showConnectedNotification(srv, stats.value)
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        notificationSyncJob?.cancel()
        orchestrator.cleanup()
        notificationManager.cancelNotification()
    }
}
