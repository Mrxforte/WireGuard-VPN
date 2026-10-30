package com.example.vpn.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.vpn.catalog.ImplementationStatus
import com.example.vpn.catalog.NetworkTechnology
import com.example.vpn.catalog.TechnologyCatalog
import com.example.vpn.catalog.TechnologyGroup

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TechnologyCatalogScreen(
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<TechnologyGroup?>(null) }
    var selectedStatus by remember { mutableStateOf<ImplementationStatus?>(null) }
    var detailTech by remember { mutableStateOf<NetworkTechnology?>(null) }

    val filteredList = remember(searchQuery, selectedGroup, selectedStatus) {
        TechnologyCatalog.ALL_100_TECHNOLOGIES.filter { tech ->
            val matchesSearch = searchQuery.isBlank() ||
                    tech.name.contains(searchQuery, ignoreCase = true) ||
                    tech.id.contains(searchQuery, ignoreCase = true) ||
                    tech.description.contains(searchQuery, ignoreCase = true) ||
                    tech.cipherSuite.contains(searchQuery, ignoreCase = true) ||
                    tech.defaultPort.contains(searchQuery, ignoreCase = true)

            val matchesGroup = selectedGroup == null || tech.group == selectedGroup
            val matchesStatus = selectedStatus == null || tech.status == selectedStatus

            matchesSearch && matchesGroup && matchesStatus
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VpnBgDark)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "100 Networking Catalog",
                        color = VpnNeonGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Protocols, Proxies, Anti-Censorship & Relays",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
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

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_input"),
                placeholder = { Text("Search 100 protocols, ciphers, ports...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = VpnNeonGreen) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VpnNeonGreen,
                    unfocusedBorderColor = VpnBorderDark,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = VpnSurfaceCard,
                    unfocusedContainerColor = VpnSurfaceCard
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Group Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = selectedGroup == null,
                    onClick = { selectedGroup = null },
                    label = { Text("All Groups (${TechnologyCatalog.ALL_100_TECHNOLOGIES.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VpnNeonGreen.copy(alpha = 0.2f),
                        selectedLabelColor = VpnNeonGreen
                    )
                )

                TechnologyGroup.values().forEach { grp ->
                    val count = TechnologyCatalog.getTechnologiesByGroup(grp).size
                    FilterChip(
                        selected = selectedGroup == grp,
                        onClick = { selectedGroup = if (selectedGroup == grp) null else grp },
                        label = { Text("${grp.title} ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VpnNeonGreen.copy(alpha = 0.2f),
                            selectedLabelColor = VpnNeonGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Status indicator count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredList.size} of 100 entries",
                    color = TextGray,
                    fontSize = 12.sp
                )
                if (selectedStatus != null || selectedGroup != null || searchQuery.isNotBlank()) {
                    Text(
                        text = "Reset filters",
                        color = VpnNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            selectedGroup = null
                            selectedStatus = null
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList, key = { it.id }) { tech ->
                TechnologyCatalogCard(
                    tech = tech,
                    onClick = { detailTech = tech }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Detail Dialog
    detailTech?.let { tech ->
        AlertDialog(
            onDismissRequest = { detailTech = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(tech.status.badgeColor),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = tech.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = tech.description, color = TextGray, fontSize = 13.sp)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(tech.status.badgeColor).copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "STATUS: ${tech.status.label}",
                            color = Color(tech.status.badgeColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(text = "Canonical ID: ${tech.id}", color = TextWhite, fontSize = 12.sp)
                    Text(text = "Layer: ${tech.osiLayer}", color = TextWhite, fontSize = 12.sp)
                    Text(text = "Default Port: ${tech.defaultPort}", color = TextWhite, fontSize = 12.sp)
                    Text(text = "Ciphers: ${tech.cipherSuite}", color = TextWhite, fontSize = 12.sp)
                    Text(text = "Deployment Role: ${tech.deploymentRole}", color = TextWhite, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { detailTech = null }) {
                    Text("Close", color = VpnNeonGreen)
                }
            },
            containerColor = VpnSurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun TechnologyCatalogCard(
    tech: NetworkTechnology,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("tech_item_${tech.id}"),
        color = VpnSurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, VpnBorderDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tech.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(tech.status.badgeColor).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tech.status.label,
                        color = Color(tech.status.badgeColor),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tech.description,
                fontSize = 12.sp,
                color = TextGray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = tech.osiLayer, fontSize = 11.sp, color = TextMuted)
                Text(text = "•", fontSize = 11.sp, color = TextMuted)
                Text(text = tech.defaultPort, fontSize = 11.sp, color = VpnNeonGreen)
                Text(text = "•", fontSize = 11.sp, color = TextMuted)
                Text(text = tech.cipherSuite.take(24) + "...", fontSize = 11.sp, color = TextGray)
            }
        }
    }
}
