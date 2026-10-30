package com.example.vpn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.ui.theme.VpnGold
import com.example.ui.theme.VpnNeonGreen
import com.example.ui.theme.VpnStopRed
import com.example.ui.theme.VpnSurfaceCard
import com.example.vpn.model.RegisteredDevice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    username: String,
    userTier: String,
    authToken: String?,
    devices: List<RegisteredDevice>,
    onLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onLogout: () -> Unit,
    onRevokeDevice: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var isAuthDialogVisible by remember { mutableStateOf(false) }
    var loginUsername by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VpnBgDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Account & Device Management",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().testTag("user_profile_card"),
                    color = VpnSurfaceCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(VpnNeonGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = VpnNeonGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = username,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = VpnGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$userTier Plan (5 Devices)",
                                    fontSize = 12.sp,
                                    color = VpnGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (authToken != null) {
                            OutlinedButton(
                                onClick = onLogout,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VpnStopRed),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VpnStopRed.copy(alpha = 0.5f))
                            ) {
                                Text("Logout", fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = { isAuthDialogVisible = true },
                                colors = ButtonDefaults.buttonColors(containerColor = VpnNeonGreen)
                            ) {
                                Text("Sign In", color = VpnBgDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Login Card if Guest
            if (isAuthDialogVisible) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = VpnSurfaceCard,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VpnNeonGreen)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Sign In with Django Account",
                                color = VpnNeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = loginUsername,
                                onValueChange = { loginUsername = it },
                                label = { Text("Username", color = TextGray) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Password", color = TextGray) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )

                            authError?.let {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = it, color = VpnStopRed, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(onClick = { isAuthDialogVisible = false }) {
                                    Text("Cancel", color = TextGray)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        onLogin(loginUsername, loginPassword) { success, err ->
                                            if (success) {
                                                isAuthDialogVisible = false
                                                authError = null
                                            } else {
                                                authError = err ?: "Sign in failed"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VpnNeonGreen)
                                ) {
                                    Text("Authenticate", color = VpnBgDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Registered Devices List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTERED DEVICES (${devices.size}/5)",
                        color = VpnNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            items(devices, key = { it.id }) { device ->
                DeviceRow(device = device, onRevoke = { onRevokeDevice(device.id) })
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun DeviceRow(device: RegisteredDevice, onRevoke: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = VpnSurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (device.isCurrentDevice) Icons.Default.Smartphone else Icons.Default.Devices,
                contentDescription = null,
                tint = if (device.isCurrentDevice) VpnNeonGreen else TextGray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = device.deviceName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    if (device.isCurrentDevice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VpnNeonGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "THIS DEVICE", color = VpnNeonGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${device.deviceModel} • ${device.lastSeenAt}",
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

            if (!device.isCurrentDevice) {
                IconButton(onClick = onRevoke) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Revoke device",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
