package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DropCampaign
import com.example.data.DropStage
import com.example.ui.components.HoloBadge
import com.example.ui.components.VotingOptionCard
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

@Composable
fun DropsScreen(
    campaigns: List<DropCampaign>,
    onVoteOption: (campaignId: String, optionId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStageFilter by remember { mutableStateOf<DropStage?>(null) }

    val filteredCampaigns = remember(campaigns, selectedStageFilter) {
        if (selectedStageFilter == null) campaigns
        else campaigns.filter { it.stage == selectedStageFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KikiDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Stage Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMMUNITY DROPS",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = KikiPearl
                    )
                    HoloBadge(
                        text = "DEMOCRATIZED BEAUTY",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.HowToVote,
                                contentDescription = null,
                                tint = KikiLilac,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cast your vote to decide the next cosmetic drops entering physical manufacturing.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KikiTextMuted
                )
            }
        }

        // Filter chips by drop stage
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedStageFilter == null,
                        onClick = { selectedStageFilter = null },
                        label = { Text("All Stages (${campaigns.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KikiLilac,
                            selectedLabelColor = Color.Black,
                            containerColor = KikiSurfaceDark,
                            labelColor = KikiPearl
                        )
                    )
                }
                items(DropStage.values()) { stage ->
                    val isSelected = selectedStageFilter == stage
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStageFilter = stage },
                        label = { Text(stage.label) },
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

        // Campaign items with embedded interactive voting
        items(filteredCampaigns) { campaign ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HoloBadge(text = campaign.dropNumber)
                        Text(
                            text = "⏳ ${campaign.hoursRemaining}h left",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = campaign.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = KikiPearl
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = campaign.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = KikiTextMuted
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = campaign.fullDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = KikiTextMuted.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Co-Creator Tally
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CURRENT OPTIONS (${campaign.options.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = KikiLilac
                        )
                        Text(
                            text = "🗳️ ${campaign.totalVotes} Total Votes",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Options list
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        campaign.options.forEach { option ->
                            val isUserVote = campaign.userVotedOptionId == option.id
                            VotingOptionCard(
                                option = option,
                                totalVotesInCampaign = campaign.totalVotes,
                                isUserVote = isUserVote,
                                onVote = { onVoteOption(campaign.id, option.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Production timeline status pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KikiSurfaceElevated, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Production Target: ${campaign.productionDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                        Text(
                            text = campaign.stage.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiCyan
                        )
                    }
                }
            }
        }
    }
}
