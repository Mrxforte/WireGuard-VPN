package com.example.vpn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VpnBgDark
import com.example.ui.theme.VpnBorderDark
import com.example.ui.theme.VpnNeonGreen
import com.example.ui.theme.VpnSurfaceCard
import com.example.vpn.model.SmartConnectionSettings
import com.example.vpn.model.VpnProtocol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSettingsScreen(
    settings: SmartConnectionSettings,
    backendUrl: String,
    onSaveSettings: (SmartConnectionSettings) -> Unit,
    onSaveBackendUrl: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var autoServer by remember(settings) { mutableStateOf(settings.autoServerSelection) }
    var preferredProto by remember(settings) { mutableStateOf(settings.preferredProtocol) }
    var autoFallback by remember(settings) { mutableStateOf(settings.autoProtocolFallback) }
    var autoFailover by remember(settings) { mutableStateOf(settings.autoServerFailover) }
    var strictPrivacy by remember(settings) { mutableStateOf(settings.strictPrivacyMode) }
    var reconnectOnNet by remember(settings) { mutableStateOf(settings.reconnectOnNetworkChange) }
    var maxRetries by remember(settings) { mutableStateOf(settings.maxRetryCount) }

    var urlInput by remember(backendUrl) { mutableStateOf(backendUrl) }

    fun commitSettings() {
        onSaveSettings(
            settings.copy(
                autoServerSelection = autoServer,
                preferredProtocol = preferredProto,
                autoProtocolFallback = autoFallback,
                autoServerFailover = autoFailover,
                strictPrivacyMode = strictPrivacy,
                reconnectOnNetworkChange = reconnectOnNet,
                maxRetryCount = maxRetries
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VpnBgDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Smart Connection Settings",
                    color = VpnNeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = VpnBgDark)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Protocol Selection Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = VpnSurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PREFERRED PROTOCOL",
                        color = VpnNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VpnProtocol.values().forEach { proto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    preferredProto = proto
                                    commitSettings()
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = preferredProto == proto,
                                onClick = {
                                    preferredProto = proto
                                    commitSettings()
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = VpnNeonGreen,
                                    unselectedColor = TextGray
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${proto.displayName} (${proto.transport} :${proto.defaultPort})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = proto.description,
                                    fontSize = 11.sp,
                                    color = TextGray
                                )
                            }
                        }
                    }
                }
            }

            // Smart Failover & Routing Policies
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = VpnSurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INTELLIGENT ROUTING & FAILOVER",
                        color = VpnNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = "Automatic Server Ranking",
                        subtitle = "Selects optimal node using (Ping * 0.6 + Load * 0.4)",
                        checked = autoServer,
                        onCheckedChange = {
                            autoServer = it
                            commitSettings()
                        }
                    )

                    SettingToggleRow(
                        title = "Protocol Fallback",
                        subtitle = "Automatically falls back: WireGuard -> OpenVPN UDP -> TCP 443",
                        checked = autoFallback,
                        onCheckedChange = {
                            autoFallback = it
                            commitSettings()
                        }
                    )

                    SettingToggleRow(
                        title = "Server Failover on Loss",
                        subtitle = "Quarantines unresponsive server and migrates seamlessly",
                        checked = autoFailover,
                        onCheckedChange = {
                            autoFailover = it
                            commitSettings()
                        }
                    )

                    SettingToggleRow(
                        title = "Strict Privacy Mode",
                        subtitle = "Fail-closed: blocks connection if any leak check fails",
                        checked = strictPrivacy,
                        onCheckedChange = {
                            strictPrivacy = it
                            commitSettings()
                        }
                    )

                    SettingToggleRow(
                        title = "Reconnect on Network Switch",
                        subtitle = "Seamlessly re-keys session between Wi-Fi and Cellular",
                        checked = reconnectOnNet,
                        onCheckedChange = {
                            reconnectOnNet = it
                            commitSettings()
                        }
                    )
                }
            }

            // Android System Kill Switch Guidance
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = VpnSurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = VpnNeonGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYSTEM KILL SWITCH (ALWAYS-ON VPN)",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "To guarantee zero packet leakage at the Android OS kernel level, go to Android Settings -> Network & internet -> VPN -> SmartVPN and enable 'Always-on VPN' and 'Block connections without VPN'.",
                        fontSize = 12.sp,
                        color = TextGray,
                        lineHeight = 17.sp
                    )
                }
            }

            // Self-Hosted Django Backend Configuration
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = VpnSurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CONTROL PLANE BACKEND",
                        color = VpnNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("backend_url_input"),
                        label = { Text("Django REST API Base URL", color = TextGray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VpnNeonGreen,
                            unfocusedBorderColor = VpnBorderDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onSaveBackendUrl(urlInput) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = VpnNeonGreen)
                    ) {
                        Text("Save Control Plane URL", color = VpnBgDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(text = subtitle, fontSize = 11.sp, color = TextGray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VpnBgDark,
                checkedTrackColor = VpnNeonGreen,
                uncheckedThumbColor = TextGray,
                uncheckedTrackColor = VpnBorderDark
            )
        )
    }
}
