package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActivityType
import com.example.data.CreatorActivityItem
import com.example.data.CreatorProfile
import com.example.data.TierMilestone
import com.example.ui.components.HoloBadge
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiRose
import com.example.ui.theme.KikiSuccess
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsDashboardScreen(
    profile: CreatorProfile,
    activities: List<CreatorActivityItem>,
    milestones: List<TierMilestone>,
    selectedFilter: ActivityType?,
    onFilterSelected: (ActivityType?) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredActivities = remember(activities, selectedFilter) {
        if (selectedFilter == null) activities
        else activities.filter { it.type == selectedFilter }
    }

    // Next tier progress calculation
    val currentTierMilestone = milestones.find { it.isCurrent }
    val nextTierMilestone = milestones.find { it.level == (profile.tierLevel + 1) }
    val nextTargetSparks = nextTierMilestone?.requiredSparks ?: 2500
    val progressFraction = (profile.sparksBalance.toFloat() / nextTargetSparks.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "tierProgress"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(KikiDarkBg),
        containerColor = KikiDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CREATOR REWARDS & HISTORY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = KikiPearl
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClicked,
                        modifier = Modifier.testTag("dashboard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = KikiPearl
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KikiDarkBg)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Accumulated Rewards Overview
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HoloBadge(
                                text = "ACCUMULATED REWARDS",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = KikiGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            )
                            Text(
                                text = profile.handle,
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large KPI Balance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Current Balance",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = KikiTextMuted
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = KikiGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${profile.sparksBalance}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        ),
                                        color = KikiPearl
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SPARKS",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = KikiGold
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Lifetime Earned",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiTextMuted
                                )
                                Text(
                                    text = "${profile.lifetimeSparksEarned} Sparks",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Activity Stat Bubbles
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(KikiSurfaceElevated, RoundedCornerShape(14.dp))
                                .padding(vertical = 12.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${profile.totalVotesCast}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiLilac
                                )
                                Text(
                                    text = "Drop Votes",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiTextMuted
                                )
                            }
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(KikiSurfaceBorder))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${profile.totalFormulasCreated}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiCyan
                                )
                                Text(
                                    text = "Blends Crafted",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiTextMuted
                                )
                            }
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(KikiSurfaceBorder))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${profile.streakDays}d",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiRose
                                )
                                Text(
                                    text = "Daily Streak",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiTextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Creator Tier Milestone & Perks Roadmap
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = KikiCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tier ${profile.tierLevel}: ${profile.tierName}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                            }
                            HoloBadge(text = "${(progressFraction * 100).toInt()}% to Next Tier")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar to next tier
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(KikiSurfaceBorder)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedProgress)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(HolographicGradient)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${profile.sparksBalance} Sparks",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiTextMuted
                            )
                            Text(
                                text = "Next: ${nextTierMilestone?.name ?: "Max"} (${nextTargetSparks} Sparks)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "ACTIVE TIER 3 PERKS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = KikiLilac
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        currentTierMilestone?.perks?.forEach { perk ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = KikiSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = perk,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KikiPearl
                                )
                            }
                        }
                    }
                }
            }

            // Activity History Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = KikiLilac,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ACTIVITY HISTORY",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = KikiPearl
                        )
                    }
                    Text(
                        text = "${filteredActivities.size} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = KikiTextMuted
                    )
                }
            }

            // Filter Chips for Activity Types
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { onFilterSelected(null) },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KikiLilac,
                                selectedLabelColor = Color.Black,
                                containerColor = KikiSurfaceDark,
                                labelColor = KikiPearl
                            )
                        )
                    }
                    items(ActivityType.values()) { type ->
                        val isSelected = selectedFilter == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterSelected(type) },
                            label = { Text("${type.icon} ${type.label}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KikiLilac,
                                selectedLabelColor = Color.Black,
                                containerColor = KikiSurfaceDark,
                                labelColor = KikiPearl
                            )
                        )
                    }
                }
            }

            // Timeline Items
            if (filteredActivities.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No activities matching this filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KikiTextMuted
                        )
                    }
                }
            } else {
                items(filteredActivities) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Medallion
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(KikiSurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.type.icon,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KikiTextMuted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.timeAgo,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = KikiCyan
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Delta Badge
                            val isPositive = item.sparksDelta >= 0
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isPositive) KikiSuccess.copy(alpha = 0.15f) else KikiGold.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isPositive) KikiSuccess else KikiGold
                                )
                            ) {
                                Text(
                                    text = if (isPositive) "+${item.sparksDelta}" else "${item.sparksDelta}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = if (isPositive) KikiSuccess else KikiGold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
