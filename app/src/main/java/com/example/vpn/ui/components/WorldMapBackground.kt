package com.example.vpn.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.ui.theme.VpnNeonGreen

/**
 * Stylized emerald world map background with pulsing network nodes,
 * matching the Satellite VPN UI reference image.
 */
@Composable
fun WorldMapBackground(
    modifier: Modifier = Modifier.fillMaxWidth().height(240.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mapNodes")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val mapColor = VpnNeonGreen.copy(alpha = 0.22f)

        // North America
        val naPath = Path().apply {
            moveTo(w * 0.12f, h * 0.22f)
            lineTo(w * 0.24f, h * 0.18f)
            lineTo(w * 0.32f, h * 0.24f)
            lineTo(w * 0.29f, h * 0.38f)
            lineTo(w * 0.22f, h * 0.46f)
            lineTo(w * 0.17f, h * 0.44f)
            lineTo(w * 0.11f, h * 0.30f)
            close()
        }
        drawPath(naPath, mapColor)

        // South America
        val saPath = Path().apply {
            moveTo(w * 0.23f, h * 0.52f)
            lineTo(w * 0.29f, h * 0.53f)
            lineTo(w * 0.33f, h * 0.64f)
            lineTo(w * 0.27f, h * 0.82f)
            lineTo(w * 0.22f, h * 0.70f)
            close()
        }
        drawPath(saPath, mapColor)

        // Europe
        val euPath = Path().apply {
            moveTo(w * 0.45f, h * 0.22f)
            lineTo(w * 0.54f, h * 0.20f)
            lineTo(w * 0.53f, h * 0.34f)
            lineTo(w * 0.44f, h * 0.36f)
            close()
        }
        drawPath(euPath, mapColor)

        // Africa
        val afPath = Path().apply {
            moveTo(w * 0.44f, h * 0.40f)
            lineTo(w * 0.56f, h * 0.42f)
            lineTo(w * 0.53f, h * 0.68f)
            lineTo(w * 0.47f, h * 0.75f)
            lineTo(w * 0.42f, h * 0.54f)
            close()
        }
        drawPath(afPath, mapColor)

        // Asia
        val asPath = Path().apply {
            moveTo(w * 0.56f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.16f)
            lineTo(w * 0.84f, h * 0.28f)
            lineTo(w * 0.76f, h * 0.48f)
            lineTo(w * 0.65f, h * 0.52f)
            lineTo(w * 0.58f, h * 0.36f)
            close()
        }
        drawPath(asPath, mapColor)

        // Australia
        val auPath = Path().apply {
            moveTo(w * 0.76f, h * 0.65f)
            lineTo(w * 0.88f, h * 0.63f)
            lineTo(w * 0.86f, h * 0.78f)
            lineTo(w * 0.74f, h * 0.76f)
            close()
        }
        drawPath(auPath, mapColor)

        // Key Network Nodes (Pulsing Green Dots)
        val nodes = listOf(
            Offset(w * 0.22f, h * 0.32f), // US East
            Offset(w * 0.15f, h * 0.35f), // US West
            Offset(w * 0.48f, h * 0.27f), // UK / Germany
            Offset(w * 0.52f, h * 0.29f), // Frankfurt / Paris
            Offset(w * 0.72f, h * 0.35f), // Tokyo
            Offset(w * 0.68f, h * 0.52f), // Singapore
            Offset(w * 0.82f, h * 0.70f), // Sydney
            Offset(w * 0.28f, h * 0.66f)  // Brazil
        )

        nodes.forEach { node ->
            // Outer glow
            drawCircle(
                color = VpnNeonGreen.copy(alpha = pulseAlpha * 0.4f),
                radius = 8.dp.toPx(),
                center = node
            )
            // Core
            drawCircle(
                color = VpnNeonGreen.copy(alpha = pulseAlpha),
                radius = 3.5.dp.toPx(),
                center = node
            )
        }
    }
}
