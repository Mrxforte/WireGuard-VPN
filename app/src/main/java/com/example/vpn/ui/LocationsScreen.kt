package com.example.vpn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
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
import com.example.vpn.model.VpnServer

/**
 * Screen 4 from the reference mockup: Full Country & Service Selection screen.
 * Displays search bar, service categories (Streaming, Gaming, P2P, Double VPN),
 * and national servers with signal bars and radio toggles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    servers: List<VpnServer>,
    selectedServer: VpnServer,
    onServerSelected: (VpnServer) -> Unit,
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedServiceCategory by remember { mutableStateOf("All") }

    val serviceCategories = listOf("All", "Streaming", "Gaming", "P2P", "Double VPN")

    val filteredServers = remember(servers, searchQuery, selectedServiceCategory) {
        servers.filter { server ->
            val matchesSearch = server.name.contains(searchQuery, ignoreCase = true) ||
                    server.country.contains(searchQuery, ignoreCase = true) ||
                    server.city.contains(searchQuery, ignoreCase = true) ||
                    server.serviceLabel.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedServiceCategory) {
                "Streaming" -> server.serviceCategory == "streaming"
                "Gaming" -> server.serviceCategory == "gaming"
                "P2P" -> server.serviceCategory == "p2p"
                "Double VPN" -> server.serviceCategory == "double_vpn"
                else -> true
            }

            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VpnBgDark)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Satellite VPN",
                    color = VpnNeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("locations_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = VpnNeonGreen
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* signal indicator */ }) {
                    Icon(
                        imageVector = Icons.Default.CellTower,
                        contentDescription = "Signal",
                        tint = VpnNeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = VpnBgDark)
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // Search Bar matching mockup
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("locations_search_input"),
                placeholder = { Text("Search location", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VpnNeonGreen,
                    unfocusedBorderColor = VpnBorderDark,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = VpnSurfaceCard,
                    unfocusedContainerColor = VpnSurfaceCard
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Service Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(serviceCategories) { cat ->
                    val isSelected = cat == selectedServiceCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedServiceCategory = cat },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VpnNeonGreen.copy(alpha = 0.2f),
                            selectedLabelColor = VpnNeonGreen,
                            containerColor = VpnSurfaceCard,
                            labelColor = TextGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) VpnNeonGreen else VpnBorderDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Country Server List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filteredServers, key = { it.id }) { server ->
                    val isSelected = server.id == selectedServer.id
                    CountryServerRowItem(
                        server = server,
                        isSelected = isSelected,
                        onClick = { onServerSelected(server) }
                    )
                }
            }
        }
    }
}

@Composable
fun CountryServerRowItem(
    server: VpnServer,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("country_row_${server.id}"),
        color = VpnSurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) VpnNeonGreen.copy(alpha = 0.8f) else VpnBorderDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag in circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F1612)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = server.flagEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Country Name & Specialized Service
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = server.country,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = server.city,
                        fontSize = 11.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${server.serviceLabel}",
                        fontSize = 11.sp,
                        color = VpnNeonGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Signal strength icon (green bars)
            Icon(
                imageVector = Icons.Default.SignalCellularAlt,
                contentDescription = "Signal",
                tint = if (server.pingMs < 50) VpnNeonGreen else Color(0xFFFFB300),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Radio toggle button matching mockup
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) VpnNeonGreen else TextMuted, CircleShape)
                    .background(if (isSelected) VpnNeonGreen.copy(alpha = 0.2f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(VpnNeonGreen)
                    )
                }
            }
        }
    }
}
