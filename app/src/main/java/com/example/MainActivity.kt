package com.example

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VpnBgDark
import com.example.vpn.model.SecurityLeakStatus
import com.example.vpn.model.VpnSessionState
import com.example.vpn.ui.AccountScreen
import com.example.vpn.ui.BackendSettingsDialog
import com.example.vpn.ui.DiagnosticsScreen
import com.example.vpn.ui.InfrastructureGuideDialog
import com.example.vpn.ui.LocationsScreen
import com.example.vpn.ui.PremiumScreen
import com.example.vpn.ui.SmartSettingsScreen
import com.example.vpn.ui.TechnologyCatalogScreen
import com.example.vpn.ui.VpnDashboardScreen
import com.example.vpn.ui.VpnViewModel
import com.example.vpn.ui.components.FloatingBottomBar

class MainActivity : ComponentActivity() {

    private val viewModel: VpnViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                VpnAppRoot(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnAppRoot(viewModel: VpnViewModel) {
    val connectionState by viewModel.connectionState.collectAsState()
    val activeProtocol by viewModel.activeProtocol.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val servers by viewModel.servers.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val diagnostics by viewModel.diagnostics.collectAsState()
    val smartSettings by viewModel.smartSettings.collectAsState()
    val backendUrl by viewModel.backendUrl.collectAsState()
    val authToken by viewModel.authToken.collectAsState()
    val username by viewModel.username.collectAsState()
    val userTier by viewModel.userTier.collectAsState()
    val registeredDevices by viewModel.registeredDevices.collectAsState()
    val uiError by viewModel.uiErrorMessage.collectAsState()
    val vpnPermissionIntent by viewModel.vpnPermissionIntent.collectAsState()

    // Active bottom navigation tab:
    // 0: Dashboard
    // 1: Servers
    // 2: 100 Catalog
    // 3: Diagnostics
    // 4: Settings
    var currentTab by remember { mutableIntStateOf(0) }

    var showInfraGuideDialog by remember { mutableStateOf(false) }

    // Handle system back button to return to home dashboard
    if (currentTab != 0) {
        BackHandler {
            currentTab = 0
        }
    }

    // Android VpnService permission prompt launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val isGranted = result.resultCode == Activity.RESULT_OK
        viewModel.onVpnPermissionResult(isGranted)
    }

    // Android 13+ Notification permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(vpnPermissionIntent) {
        vpnPermissionIntent?.let { intent ->
            vpnPermissionLauncher.launch(intent)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = VpnBgDark,
        bottomBar = {
            FloatingBottomBar(
                currentTabIndex = currentTab,
                onTabSelected = { currentTab = it },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentTab) {
                0 -> {
                    // Main Dashboard
                    VpnDashboardScreen(
                        connectionState = connectionState,
                        activeProtocol = activeProtocol,
                        selectedServer = selectedServer,
                        allServers = servers,
                        stats = stats,
                        securityStatus = SecurityLeakStatus(),
                        peerConfig = null,
                        leaseSeconds = 86400,
                        errorMessage = uiError,
                        onConnectToggle = {
                            if (connectionState.isStableConnected) {
                                viewModel.disconnect()
                            } else {
                                viewModel.startConnection()
                            }
                        },
                        onChangeServerClick = { currentTab = 1 },
                        onOpenSettingsClick = { currentTab = 4 },
                        onOpenInfraGuideClick = { showInfraGuideDialog = true },
                        onOpenDiagnosticsClick = { currentTab = 3 },
                        onOpenCatalogClick = { currentTab = 2 },
                        onGoPremiumClick = { currentTab = 4 },
                        onOptimalLocationClick = { server ->
                            viewModel.selectServer(server)
                        },
                        onClearError = { viewModel.clearError() }
                    )
                }
                1 -> {
                    // Server Locations List
                    LocationsScreen(
                        servers = servers,
                        selectedServer = selectedServer,
                        onServerSelected = { server ->
                            viewModel.selectServer(server)
                            currentTab = 0
                        },
                        onBackClick = { currentTab = 0 }
                    )
                }
                2 -> {
                    // 100 Networking Catalog
                    TechnologyCatalogScreen(
                        onBackClick = { currentTab = 0 }
                    )
                }
                3 -> {
                    // Connection Quality Diagnostics
                    DiagnosticsScreen(
                        diagnostics = diagnostics,
                        onBackClick = { currentTab = 0 }
                    )
                }
                4 -> {
                    // Smart Settings
                    SmartSettingsScreen(
                        settings = smartSettings,
                        backendUrl = backendUrl,
                        onSaveSettings = { updated -> viewModel.updateSettings(updated) },
                        onSaveBackendUrl = { url -> viewModel.updateBackendUrl(url) },
                        onBackClick = { currentTab = 0 }
                    )
                }
            }
        }
    }

    // Server Infrastructure Deployment Guide Dialog
    if (showInfraGuideDialog) {
        InfrastructureGuideDialog(
            onDismiss = { showInfraGuideDialog = false }
        )
    }
}
