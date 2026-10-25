package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Pixel-perfect colors matching the user's reference design ("Satellite VPN" / "Family Protect VPN")
val VpnBgDark = Color(0xFF090E0C)         // Deep obsidian with emerald tint
val VpnSurfaceDark = Color(0xFF111714)    // Dark card surface
val VpnSurfaceCard = Color(0xFF161F1A)    // Slightly lighter card container
val VpnSurfaceHighlight = Color(0xFF1D2923) // Selected/highlighted surface
val VpnBorderDark = Color(0xFF23332B)     // Subtle green-charcoal border

val VpnNeonGreen = Color(0xFF00E676)      // Vibrant neon green power/accent
val VpnNeonGreenLight = Color(0xFF69F0AE)
val VpnGreenGlow = Color(0x3300E676)
val VpnGreenDeep = Color(0xFF00897B)

val VpnGold = Color(0xFFFFB300)           // Crown / "GO PREMIUM" gold
val VpnGoldBg = Color(0x26FFB300)

val VpnStopRed = Color(0xFFE53935)        // "STOP" disconnect red
val VpnStopRedGlow = Color(0x33E53935)

val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF90A499)
val TextMuted = Color(0xFF5A6E63)

// Backward compatible aliases
val VpnGreenLight = Color(0xFF69F0AE)
val VpnPrimary = VpnNeonGreen
val VpnSecondary = VpnGreenLight
val VpnTertiary = VpnNeonGreen
val VpnWarning = VpnGold
val VpnError = VpnStopRed
val DarkBackground = VpnBgDark
val DarkSurface = VpnSurfaceDark
val DarkSurfaceVariant = VpnSurfaceCard
val DarkBorder = VpnBorderDark
val TextPrimary = TextWhite
val TextSecondary = TextGray
