package com.example.vpn.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VpnNeonGreen
import com.example.ui.theme.VpnStopRed
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Speedometer gauge matching Screen 3 of the reference mockup.
 * Displays arc with 0, 25, 50, 75, 100, dynamic needle, live speed readout,
 * and a red STOP button to cleanly disconnect.
 */
@Composable
fun SpeedometerGauge(
    currentSpeedMbps: Float,
    maxSpeedMbps: Float = 100f,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clampedSpeed = currentSpeedMbps.coerceIn(0f, maxSpeedMbps)
    val animatedSpeed by animateFloatAsState(
        targetValue = clampedSpeed,
        animationSpec = tween(durationMillis = 600),
        label = "animatedSpeed"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(220.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = (size.width / 2) - 20.dp.toPx()

                // Background track arc (from 140° to 40°, sweep 260°)
                val startAngle = 140f
                val sweepAngle = 260f

                drawArc(
                    color = Color(0xFF192A21),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )

                // Active glowing progress arc
                val progressAngle = (animatedSpeed / maxSpeedMbps) * sweepAngle
                if (progressAngle > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                VpnNeonGreen.copy(alpha = 0.6f),
                                VpnNeonGreen,
                                Color(0xFF69F0AE)
                            )
                        ),
                        startAngle = startAngle,
                        sweepAngle = progressAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Ticks along the arc (0, 25, 50, 75, 100)
                val tickFractions = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
                tickFractions.forEach { frac ->
                    val angleDeg = startAngle + frac * sweepAngle
                    val angleRad = Math.toRadians(angleDeg.toDouble())
                    val innerR = radius - 16.dp.toPx()
                    val outerR = radius - 4.dp.toPx()

                    val startPoint = Offset(
                        (center.x + innerR * cos(angleRad)).toFloat(),
                        (center.y + innerR * sin(angleRad)).toFloat()
                    )
                    val endPoint = Offset(
                        (center.x + outerR * cos(angleRad)).toFloat(),
                        (center.y + outerR * sin(angleRad)).toFloat()
                    )

                    val isPassed = animatedSpeed >= frac * maxSpeedMbps
                    drawLine(
                        color = if (isPassed) VpnNeonGreen else Color(0xFF334B3D),
                        start = startPoint,
                        end = endPoint,
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Needle
                val needleAngle = startAngle + (animatedSpeed / maxSpeedMbps) * sweepAngle
                val needleRad = Math.toRadians(needleAngle.toDouble())
                val needleLength = radius - 24.dp.toPx()

                val needleTip = Offset(
                    (center.x + needleLength * cos(needleRad)).toFloat(),
                    (center.y + needleLength * sin(needleRad)).toFloat()
                )

                drawLine(
                    color = Color.White,
                    start = center,
                    end = needleTip,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Needle pivot
                drawCircle(color = VpnNeonGreen, radius = 7.dp.toPx(), center = center)
                drawCircle(color = Color.Black, radius = 3.5.dp.toPx(), center = center)
            }

            // Center speed readout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 70.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "%.1f", animatedSpeed),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Mbps",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Red STOP button matching mockup
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(VpnStopRed.copy(alpha = 0.2f))
                .border(1.5.dp, VpnStopRed, RoundedCornerShape(20.dp))
                .clickable(onClick = onStopClick)
                .padding(horizontal = 28.dp, vertical = 7.dp)
                .testTag("stop_vpn_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "STOP",
                color = VpnStopRed,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
