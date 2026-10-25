package com.example.vpn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VpnPrimary
import com.example.ui.theme.VpnTertiary

@Composable
fun InfrastructureGuideDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = VpnPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Server Infrastructure",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Exact configuration for deploying the FastAPI WireGuard backend and Linux WireGuard gateway:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                // Step 1: Docker
                InfrastructureCard(
                    title = "1. Docker One-Command Run",
                    content = """
docker run -d \
  --name wireguard-fastapi \
  --cap-add=NET_ADMIN \
  --cap-add=SYS_MODULE \
  -p 8000:8000 \
  -p 51820:51820/udp \
  -v /lib/modules:/lib/modules:ro \
  fastapi-wireguard:latest
                    """.trimIndent()
                )

                // Step 2: Linux Host Kernel Setup
                InfrastructureCard(
                    title = "2. Linux WireGuard Gateway",
                    content = """
# Enable IP Forwarding
echo "net.ipv4.ip_forward=1" >> /etc/sysctl.conf
echo "net.ipv6.conf.all.forwarding=1" >> /etc/sysctl.conf
sysctl -p

# Configure NAT Masquerade
iptables -t nat -A POSTROUTING -s 10.8.0.0/24 -o eth0 -j MASQUERADE
ip6tables -t nat -A POSTROUTING -s fd00:8::/64 -o eth0 -j MASQUERADE
                    """.trimIndent()
                )

                // Step 3: API Specification
                InfrastructureCard(
                    title = "3. Required FastAPI REST Endpoints",
                    content = """
• POST /auth/token (OAuth2 username/password)
• GET  /servers (List of active nodes)
• POST /vpn/peer (Dynamic peer creation)
• POST /vpn/peer/{id}/renew (Extend lease)
• DELETE /vpn/peer/{id} (Revoke peer)
                    """.trimIndent()
                )

                // Leak Prevention Architecture Note
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = VpnTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Zero-Leak Dual Stack Guarantee",
                                color = VpnTertiary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Client enforces 0.0.0.0/0 (IPv4) and ::/0 (IPv6) in AllowedIPs to prevent IPv6 bypass leakage, plus Cloudflare 1.1.1.1 / 1.0.0.1 tunnel DNS.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_infra_guide_button"),
                colors = ButtonDefaults.buttonColors(containerColor = VpnPrimary)
            ) {
                Text("Got It", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun InfrastructureCard(title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = VpnPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text(
                    text = content,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
