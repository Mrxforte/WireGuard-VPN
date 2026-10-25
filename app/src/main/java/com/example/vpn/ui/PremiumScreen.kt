package com.example.vpn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VpnBgDark
import com.example.ui.theme.VpnBorderDark
import com.example.ui.theme.VpnGold
import com.example.ui.theme.VpnNeonGreen
import com.example.ui.theme.VpnSurfaceCard
import com.example.ui.theme.VpnSurfaceHighlight

/**
 * Screen 5 matching the reference mockup: Premium Subscription Screen.
 * Feature grid (Anonymous, Fast, Remove Ads, Secure) and 3-tier pricing plans.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onBackClick: () -> Unit,
    onPlanSubscribed: (String) -> Unit
) {
    var selectedPlanIndex by remember { mutableIntStateOf(1) } // 6 months selected by default

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VpnBgDark)
    ) {
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
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("premium_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = VpnNeonGreen
                    )
                }
            },
            actions = {
                IconButton(onClick = {}) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Crown in Glowing Circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF2C4035),
                                VpnSurfaceCard
                            )
                        )
                    )
                    .border(1.5.dp, Color(0xFF436050), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = Color(0xFFE2E8F0),
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Get premium today",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Remove ads and unlock all location",
                fontSize = 12.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2x2 Feature Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumFeatureItem(
                    icon = Icons.Default.VisibilityOff,
                    title = "Anonymous",
                    desc = "Hide your ip, anonymous surfing",
                    modifier = Modifier.weight(1f)
                )
                PremiumFeatureItem(
                    icon = Icons.Default.Bolt,
                    title = "Fast",
                    desc = "Up to 500 Mb/s bandwidth to explore",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumFeatureItem(
                    icon = Icons.Default.Block,
                    title = "Remove Ads",
                    desc = "Have fun surfing without annoying ads",
                    modifier = Modifier.weight(1f)
                )
                PremiumFeatureItem(
                    icon = Icons.Default.Lock,
                    title = "Secure",
                    desc = "Transfer traffic via encrypted tunnel",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 3 Subscription Plans
            PlanOptionCard(
                duration = "1 Month",
                billedText = "total price $9.92",
                pricePerMonth = "9.92",
                isSelected = selectedPlanIndex == 0,
                onClick = { selectedPlanIndex = 0 },
                tag = "plan_1m"
            )

            Spacer(modifier = Modifier.height(10.dp))

            PlanOptionCard(
                duration = "6 Month",
                billedText = "total price $43.14",
                pricePerMonth = "7.19",
                isSelected = selectedPlanIndex == 1,
                onClick = { selectedPlanIndex = 1 },
                tag = "plan_6m"
            )

            Spacer(modifier = Modifier.height(10.dp))

            PlanOptionCard(
                duration = "12 Month",
                billedText = "total price $79.08",
                pricePerMonth = "6.59",
                isSelected = selectedPlanIndex == 2,
                onClick = { selectedPlanIndex = 2 },
                tag = "plan_12m"
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Get Premium Now Button
            Button(
                onClick = {
                    val plan = when (selectedPlanIndex) {
                        0 -> "1_month"
                        1 -> "6_month"
                        else -> "12_month"
                    }
                    onPlanSubscribed(plan)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("get_premium_now_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VpnNeonGreen)
            ) {
                Text(
                    text = "Get Premium Now  >",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Restore purchase",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier
                    .clickable { /* Restore */ }
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun PremiumFeatureItem(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(VpnSurfaceCard)
            .border(1.dp, VpnBorderDark, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VpnNeonGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 10.sp,
                color = TextGray,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun PlanOptionCard(
    duration: String,
    billedText: String,
    pricePerMonth: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        color = if (isSelected) VpnSurfaceHighlight else VpnSurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) VpnNeonGreen else VpnBorderDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = duration,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = billedText,
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = pricePerMonth,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "$/month",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Radio toggle
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
