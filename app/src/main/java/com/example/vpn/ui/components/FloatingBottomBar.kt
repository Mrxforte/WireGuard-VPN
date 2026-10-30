package com.example.vpn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VpnBorderDark
import com.example.ui.theme.VpnNeonGreen
import com.example.ui.theme.VpnSurfaceCard

/**
 * Floating bottom navigation bar matching the Satellite VPN aesthetic.
 * Tabs:
 * 0: Dashboard
 * 1: Servers / Locations
 * 2: 100 Networking Catalog
 * 3: Quality Gate Diagnostics
 * 4: Settings & Account
 */
@Composable
fun FloatingBottomBar(
    currentTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(VpnSurfaceCard)
                .border(1.dp, VpnBorderDark, RoundedCornerShape(32.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                contentDescription = "Home",
                isSelected = currentTabIndex == 0,
                testTag = "nav_tab_home",
                onClick = { onTabSelected(0) }
            )
            BottomNavItem(
                icon = Icons.Default.Public,
                contentDescription = "Servers",
                isSelected = currentTabIndex == 1,
                testTag = "nav_tab_servers",
                onClick = { onTabSelected(1) }
            )
            BottomNavItem(
                icon = Icons.Default.MenuBook,
                contentDescription = "100 Catalog",
                isSelected = currentTabIndex == 2,
                testTag = "nav_tab_catalog",
                onClick = { onTabSelected(2) }
            )
            BottomNavItem(
                icon = Icons.Default.Analytics,
                contentDescription = "Diagnostics",
                isSelected = currentTabIndex == 3,
                testTag = "nav_tab_diagnostics",
                onClick = { onTabSelected(3) }
            )
            BottomNavItem(
                icon = Icons.Default.Settings,
                contentDescription = "Settings",
                isSelected = currentTabIndex == 4,
                testTag = "nav_tab_settings",
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF23352B) else Color.Transparent)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isSelected) VpnNeonGreen else TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
