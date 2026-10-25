package com.example.vpn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VpnPrimary
import com.example.ui.theme.VpnTertiary
import com.example.ui.theme.VpnWarning
import com.example.vpn.model.VpnServer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerSelectionSheet(
    servers: List<VpnServer>,
    selectedServer: VpnServer,
    sheetState: SheetState,
    onServerSelected: (VpnServer) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("All") }

    val regions = listOf("All", "Americas", "Europe", "Asia")

    val filteredServers = remember(servers, searchQuery, selectedRegion) {
        servers.filter { server ->
            val matchesSearch = server.name.contains(searchQuery, ignoreCase = true) ||
                    server.city.contains(searchQuery, ignoreCase = true) ||
                    server.country.contains(searchQuery, ignoreCase = true)

            val matchesRegion = when (selectedRegion) {
                "Americas" -> server.countryCode in listOf("US", "CA", "BR")
                "Europe" -> server.countryCode in listOf("DE", "NL", "GB", "FR", "CH")
                "Asia" -> server.countryCode in listOf("JP", "SG", "KR", "AU")
                else -> true
            }

            matchesSearch && matchesRegion
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Select VPN Server",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredServers.size} WireGuard locations available",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_server_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sheet",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("server_search_input"),
                placeholder = { Text("Search by country, city or host...", color = TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VpnPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Region filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(regions) { region ->
                    val isSelected = region == selectedRegion
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRegion = region },
                        label = { Text(region, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VpnPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = VpnPrimary,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) VpnPrimary else DarkBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Server List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredServers, key = { it.id }) { server ->
                    val isCurrent = server.id == selectedServer.id
                    ServerItemCard(
                        server = server,
                        isSelected = isCurrent,
                        onClick = {
                            onServerSelected(server)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ServerItemCard(
    server: VpnServer,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("server_item_${server.id}"),
        color = if (isSelected) DarkSurfaceVariant.copy(alpha = 0.9f) else DarkSurfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) VpnPrimary else DarkBorder.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag Box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = server.flagEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Server Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = server.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // WireGuard Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VpnPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "WG",
                            color = VpnPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${server.city}, ${server.countryCode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    // Ping Latency
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val pingColor = when {
                            server.pingMs < 40 -> VpnTertiary
                            server.pingMs < 80 -> VpnWarning
                            else -> Color(0xFFEF4444)
                        }
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(pingColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${server.pingMs} ms",
                            style = MaterialTheme.typography.bodySmall,
                            color = pingColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Load percentage
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(70.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { server.loadPercentage / 100f },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (server.loadPercentage > 75) VpnWarning else VpnPrimary,
                            trackColor = DarkBorder
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${server.loadPercentage}%",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Currently selected",
                    tint = VpnPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
