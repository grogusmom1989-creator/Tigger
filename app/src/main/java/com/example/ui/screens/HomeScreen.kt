package com.example.ui.screens

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CommunityFormula
import com.example.data.CreatorProfile
import com.example.data.DropCampaign
import com.example.ui.components.DropCampaignCard
import com.example.ui.components.HoloBadge
import com.example.ui.components.SwatchPill
import com.example.ui.components.TimeDisplay
import com.example.ui.components.PastelSparkle3DWorld
import com.example.ui.components.SparkleMatchGame
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiRose
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

@Composable
fun HomeScreen(
    campaigns: List<DropCampaign>,
    trendingFormulas: List<CommunityFormula>,
    profile: CreatorProfile,
    onOpenDropDetail: (DropCampaign) -> Unit,
    onNavigateToLab: () -> Unit,
    onNavigateToVault: () -> Unit,
    onRemixFormula: (CommunityFormula) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KikiDarkBg),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_kiki_studio_1793362904836),
                    contentDescription = "KIKI Beauty Lab",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dramatic Dark Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.2f),
                                    KikiDarkBg
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    HoloBadge(
                        text = "1D WORLD • DIRECTIONERS STUDIO",
                        icon = {
                            Text("❤️", fontSize = 12.sp)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cosmetics & Sparkles\nInspired by One Direction",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            lineHeight = 32.sp
                        ),
                        color = KikiPearl
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToLab,
                            modifier = Modifier
                                .testTag("hero_open_lab")
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "1D Shade Blender",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onNavigateToVault,
                            modifier = Modifier.height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KikiSurfaceElevated),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "Directioner Vault",
                                color = KikiPearl,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Prominent TimeDisplay (Eastern Time ET using device offset)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                TimeDisplay()
            }
        }

        // Three.js 3D Gentle Pastel Sparkle World
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                PastelSparkle3DWorld()
            }
        }

        // Cute Sparkle Matching Game (Stars, Crowns & Hearts)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                SparkleMatchGame()
            }
        }

        // Creator Ticker
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${profile.sparksBalance}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = KikiGold
                        )
                        Text(
                            text = "KIKI Sparks",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(KikiSurfaceBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = profile.tierName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiCyan
                        )
                        Text(
                            text = "Tier ${profile.tierLevel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(KikiSurfaceBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🔥 ${profile.streakDays}d",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiRose
                        )
                        Text(
                            text = "Lab Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }
                }
            }
        }

        // Dynamic Creator Reward Badges & Status Indicators
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = KikiLilac,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Creator Reward Badges",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = KikiLilac.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Level ${profile.tierLevel} Active",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiLilac,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RewardBadgeItem(
                            emoji = "🧪",
                            title = "Alchemist",
                            subtitle = "${profile.totalFormulasCreated} Shades",
                            isUnlocked = profile.totalFormulasCreated > 0,
                            modifier = Modifier.weight(1f)
                        )

                        RewardBadgeItem(
                            emoji = "🗳️",
                            title = "Drop Voter",
                            subtitle = "${profile.totalVotesCast} Votes",
                            isUnlocked = profile.totalVotesCast > 0,
                            modifier = Modifier.weight(1f)
                        )

                        RewardBadgeItem(
                            emoji = "⚡",
                            title = "Spark Star",
                            subtitle = "${profile.lifetimeSparksEarned} Earned",
                            isUnlocked = profile.lifetimeSparksEarned >= 500,
                            modifier = Modifier.weight(1f)
                        )

                        RewardBadgeItem(
                            emoji = "🔥",
                            title = "Streak Pro",
                            subtitle = "${profile.streakDays}d Streak",
                            isUnlocked = profile.streakDays >= 3,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }



        // Live Drops Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIVE DROP VOTING",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = KikiLilac
                    )
                    Text(
                        text = "Vote on Next Product Formulations",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                }
                HoloBadge(text = "${campaigns.size} ACTIVE")
            }
        }

        // Live Drop Cards
        items(campaigns.take(2)) { campaign ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                DropCampaignCard(
                    campaign = campaign,
                    onClick = { onOpenDropDetail(campaign) }
                )
            }
        }

        // Trending Formulations Carousel
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "COMMUNITY ALCHEMY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = KikiCyan
                    )
                    Text(
                        text = "Trending Member Formulas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trendingFormulas) { formula ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SwatchPill(
                                    primaryHex = formula.hexColor,
                                    secondaryHex = formula.secondaryHex,
                                    size = 32.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = formula.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = KikiPearl,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = formula.creatorHandle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = KikiTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Finish: ${formula.finish.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )
                            Text(
                                text = "Scent: ${formula.scentNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onRemixFormula(formula) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KikiSurfaceElevated),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Remix Formula",
                                    color = KikiLilac,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RewardBadgeItem(
    emoji: String,
    title: String,
    subtitle: String,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isUnlocked) KikiLilac.copy(alpha = 0.5f) else KikiSurfaceBorder,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) KikiSurfaceElevated else KikiSurfaceDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) HolographicGradient else Brush.linearGradient(listOf(KikiSurfaceBorder, KikiSurfaceBorder))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = if (isUnlocked) KikiPearl else KikiTextMuted,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (isUnlocked) KikiLilac else KikiTextMuted,
                maxLines = 1
            )
        }
    }
}
