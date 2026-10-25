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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VpnBgDark
import com.example.vpn.model.VpnConnectionState
import com.example.vpn.ui.BackendSettingsDialog
import com.example.vpn.ui.InfrastructureGuideDialog
import com.example.vpn.ui.LocationsScreen
import com.example.vpn.ui.PremiumScreen
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
    val selectedServer by viewModel.selectedServer.collectAsState()
    val servers by viewModel.servers.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val securityStatus by viewModel.securityStatus.collectAsState()
    val peerConfig by viewModel.currentPeerConfig.collectAsState()
    val leaseSeconds by viewModel.leaseRemainingSeconds.collectAsState()
    val uiError by viewModel.uiErrorMessage.collectAsState()
    val backendUrl by viewModel.backendUrl.collectAsState()
    val authToken by viewModel.authToken.collectAsState()
    val username by viewModel.username.collectAsState()
    val vpnPermissionIntent by viewModel.vpnPermissionIntent.collectAsState()

    // Active bottom navigation tab (0: Dashboard, 1: Locations, 2: Telemetry, 3: Premium)
    var currentTab by remember { mutableIntStateOf(0) }

    var showSettingsDialog by remember { mutableStateOf(false) }
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
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentTab) {
                0, 2 -> {
                    // Dashboard (Screen 2 Disconnected / Screen 3 Connected)
                    VpnDashboardScreen(
                        connectionState = connectionState,
                        selectedServer = selectedServer,
                        allServers = servers,
                        stats = stats,
                        securityStatus = securityStatus,
                        peerConfig = peerConfig,
                        leaseSeconds = leaseSeconds,
                        errorMessage = uiError,
                        onConnectToggle = {
                            if (connectionState == VpnConnectionState.CONNECTED) {
                                viewModel.disconnect()
                            } else {
                                viewModel.startConnection()
                            }
                        },
                        onChangeServerClick = { currentTab = 1 },
                        onOpenSettingsClick = { showSettingsDialog = true },
                        onOpenInfraGuideClick = { showInfraGuideDialog = true },
                        onGoPremiumClick = { currentTab = 3 },
                        onOptimalLocationClick = { server ->
                            viewModel.selectServer(server)
                        },
                        onClearError = { viewModel.clearError() }
                    )
                }
                1 -> {
                    // Locations List (Screen 4: 20+ countries, search bar, service filters)
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
                3 -> {
                    // Premium Screen (Screen 5: Features grid, 3-tier pricing plans, Get Premium button)
                    PremiumScreen(
                        onBackClick = { currentTab = 0 },
                        onPlanSubscribed = { _ ->
                            currentTab = 0
                        }
                    )
                }
            }
        }
    }

    // FastAPI Backend & Auth Settings Dialog
    if (showSettingsDialog) {
        BackendSettingsDialog(
            currentUrl = backendUrl,
            authToken = authToken,
            username = username,
            onSaveUrl = { url -> viewModel.updateBackendUrl(url) },
            onLogin = { user, pass ->
                viewModel.login(user, pass) { _, _ -> }
            },
            onLogout = { viewModel.logout() },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // Server Infrastructure Deployment Guide Dialog
    if (showInfraGuideDialog) {
        InfrastructureGuideDialog(
            onDismiss = { showInfraGuideDialog = false }
        )
    }
}
