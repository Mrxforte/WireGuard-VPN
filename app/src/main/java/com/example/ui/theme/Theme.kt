package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = VpnNeonGreen,
    onPrimary = Color.Black,
    primaryContainer = VpnSurfaceHighlight,
    onPrimaryContainer = VpnNeonGreen,
    secondary = VpnGreenLight,
    onSecondary = Color.Black,
    secondaryContainer = VpnSurfaceCard,
    onSecondaryContainer = TextWhite,
    tertiary = VpnGold,
    onTertiary = Color.Black,
    background = VpnBgDark,
    onBackground = TextWhite,
    surface = VpnSurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = VpnSurfaceCard,
    onSurfaceVariant = TextGray,
    outline = VpnBorderDark,
    error = VpnStopRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = VpnBgDark.toArgb()
                window.navigationBarColor = VpnBgDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
