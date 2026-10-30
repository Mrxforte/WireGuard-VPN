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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VpnBgDark
import com.example.ui.theme.VpnBorderDark
import com.example.ui.theme.VpnNeonGreen
import com.example.ui.theme.VpnStopRed
import com.example.ui.theme.VpnSurfaceCard
import com.example.vpn.model.ConnectionDiagnostics
import com.example.vpn.model.DiagnosticEvent
import com.example.vpn.model.StageResult
import com.example.vpn.model.VerificationStatus
import com.example.vpn.model.VpnSessionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    diagnostics: ConnectionDiagnostics,
    onBackClick: () -> Unit
) {
    val report = diagnostics.lastVerificationReport

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VpnBgDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Connection Quality Diagnostics",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Quality Gate Summary Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().testTag("quality_gate_summary_card"),
                    color = VpnSurfaceCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "QUALITY GATE STATUS",
                                color = TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            val stateColor = when (diagnostics.sessionState) {
                                VpnSessionState.CONNECTED -> VpnNeonGreen
                                VpnSessionState.DEGRADED -> Color(0xFFFFB74D)
                                VpnSessionState.FAILED -> VpnStopRed
                                else -> TextGray
                            }
                            Text(
                                text = diagnostics.sessionState.name,
                                color = stateColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Active Node", fontSize = 11.sp, color = TextMuted)
                                Text(text = diagnostics.activeServerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Active Protocol", fontSize = 11.sp, color = TextMuted)
                                Text(text = diagnostics.activeProtocol?.displayName ?: "None", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VpnNeonGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "DNS Resolver", fontSize = 11.sp, color = TextMuted)
                                Text(text = diagnostics.dnsHealthStatus, fontSize = 12.sp, color = TextWhite)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Tunnel Health", fontSize = 11.sp, color = TextMuted)
                                Text(text = diagnostics.tunnelHealthStatus, fontSize = 12.sp, color = TextWhite)
                            }
                        }
                    }
                }
            }

            // Quality Gate 8-Stage Pipeline Breakdown
            item {
                Text(
                    text = "8-STAGE QUALITY GATE AUDIT",
                    color = VpnNeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            if (report != null && report.stages.isNotEmpty()) {
                items(report.stages) { stage ->
                    StageResultRow(stage = stage)
                }
            } else {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = VpnSurfaceCard.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "No connection verification completed yet. Tap Connect to run the Quality Gate pipeline.",
                            color = TextGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Live Audit Event Stream
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "REAL-TIME AUDIT LOG (${diagnostics.events.size} EVENTS)",
                    color = VpnNeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            items(diagnostics.events.reversed()) { event ->
                DiagnosticEventRow(event = event)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun StageResultRow(stage: StageResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = VpnSurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (icon, color) = when (stage.status) {
                VerificationStatus.PASSED -> Icons.Default.CheckCircle to VpnNeonGreen
                VerificationStatus.DEGRADED -> Icons.Default.Warning to Color(0xFFFFB74D)
                VerificationStatus.FAILED -> Icons.Default.Error to VpnStopRed
                VerificationStatus.IN_PROGRESS -> Icons.Default.HourglassTop to Color(0xFF64B5F6)
                VerificationStatus.SKIPPED -> Icons.Default.Refresh to TextGray
            }

            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stage.stage.stageName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = stage.detailMessage,
                    fontSize = 11.sp,
                    color = if (stage.status == VerificationStatus.FAILED) VpnStopRed else TextGray
                )
                if (stage.sanitizedError != null) {
                    Text(
                        text = "Error: ${stage.sanitizedError}",
                        fontSize = 10.sp,
                        color = VpnStopRed
                    )
                }
            }

            if (stage.latencyMs > 0) {
                Text(
                    text = "${stage.latencyMs}ms",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun DiagnosticEventRow(event: DiagnosticEvent) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    val timeStr = timeFormat.format(Date(event.timestamp))

    val color = when (event.level) {
        "ERROR" -> VpnStopRed
        "WARN" -> Color(0xFFFFB74D)
        else -> VpnNeonGreen
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = VpnSurfaceCard,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = timeStr,
                fontSize = 10.sp,
                color = TextMuted,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "[${event.category}]",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = event.message,
                fontSize = 11.sp,
                color = TextWhite,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
