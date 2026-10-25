package com.example.vpn.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VpnBorderDark
import com.example.ui.theme.VpnNeonGreen
import com.example.ui.theme.VpnSurfaceCard
import java.util.Locale

/**
 * Live Upload & Download cards with filled smooth wave area charts,
 * matching Screen 3 of the reference mockup.
 */
@Composable
fun LiveWaveMetricsRow(
    uploadSpeedMbps: Float,
    downloadSpeedMbps: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WaveSpeedCard(
            title = "Upload",
            isUpload = true,
            speedMbps = uploadSpeedMbps,
            modifier = Modifier.weight(1f)
        )
        WaveSpeedCard(
            title = "Download",
            isUpload = false,
            speedMbps = downloadSpeedMbps,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun WaveSpeedCard(
    title: String,
    isUpload: Boolean,
    speedMbps: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(VpnSurfaceCard)
            .border(1.dp, VpnBorderDark, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(VpnNeonGreen.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUpload) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = VpnNeonGreen,
                        modifier = Modifier.size(11.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format(Locale.US, "%.1f Mbps", speedMbps),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Smooth Wave Graph
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                val w = size.width
                val h = size.height

                val wavePath = Path().apply {
                    moveTo(0f, h * 0.7f)
                    cubicTo(
                        w * 0.25f, h * (if (isUpload) 0.2f else 0.85f),
                        w * 0.5f, h * (if (isUpload) 0.8f else 0.15f),
                        w * 0.75f, h * 0.45f
                    )
                    cubicTo(
                        w * 0.88f, h * 0.25f,
                        w * 0.95f, h * 0.35f,
                        w, h * 0.4f
                    )
                }

                val fillPath = Path().apply {
                    addPath(wavePath)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }

                // Fill under wave
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VpnNeonGreen.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )

                // Wave stroke
                drawPath(
                    path = wavePath,
                    color = VpnNeonGreen,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
