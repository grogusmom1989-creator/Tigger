package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.LinearProgressIndicator
import com.example.util.AvatarEvolutionHelper
import com.example.ui.components.CollaborativeBlendDialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.CreatorProfile
import com.example.data.CustomCreation
import com.example.data.DailyQuest
import com.example.data.DigitalCharm
import com.example.ui.components.HoloBadge
import com.example.ui.components.SwatchPill
import com.example.ui.components.parseHexColor
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted
import com.google.firebase.auth.FirebaseUser

@Composable
fun VaultScreen(
    profile: CreatorProfile,
    creations: List<CustomCreation>,
    quests: List<DailyQuest>,
    charms: List<DigitalCharm>,
    currentUser: FirebaseUser?,
    onGoogleSignInClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
    onClaimQuest: (String) -> Unit,
    onDeleteCreation: (String) -> Unit,
    onOpenLab: () -> Unit,
    onOpenRewardsDashboard: () -> Unit = {},
    onOpenDrive: () -> Unit = {},
    onSaveSharedFormula: (CustomCreation) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedVaultTab by remember { mutableStateOf(0) }
    var showBlendDialog by remember { mutableStateOf(false) }
    val avatarState = remember(profile, creations.size, charms.size) {
        AvatarEvolutionHelper.calculateAvatarState(profile, charms.size)
    }
    val tabTitles = listOf("My Deck (${creations.size})", "Charms (${charms.size})", "Quests (${quests.size})")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KikiDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Google Sign-In & Cloud Sync Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (currentUser != null) KikiCyan.copy(alpha = 0.6f) else KikiLilac.copy(alpha = 0.6f),
                        RoundedCornerShape(22.dp)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (currentUser == null) {
                        // Unauthenticated state: show Google Sign-In callout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(KikiSurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = KikiLilac,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Google Account Sync",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                Text(
                                    text = "Sync your custom shades, sparks & votes to the cloud (Free tier)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KikiTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onGoogleSignInClicked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("google_sign_in_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "G  Sign in with Google",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    } else {
                        // Signed-in state
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(HolographicGradient)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(KikiDarkBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (currentUser.displayName ?: currentUser.email ?: "K").take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = KikiPearl
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser.displayName ?: "Verified Co-Creator",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = KikiPearl
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = "Synced",
                                        tint = KikiCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = currentUser.email ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KikiTextMuted
                                )
                            }

                            IconButton(
                                onClick = onSignOutClicked,
                                modifier = Modifier.testTag("sign_out_button")
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Sign Out",
                                    tint = KikiTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HoloBadge(text = "Cloud Deck: Synced", containerColor = KikiSurfaceElevated)
                            Text(
                                text = "Spark Plan (0 Credits Used)",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiCyan
                            )
                        }
                    }
                }
            }
        }

        // AI Studio ZIP Backup Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(KikiCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = null, tint = KikiCyan, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("AI Studio ZIP Backup", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = KikiPearl)
                            Text("Archive ${creations.size} formulas & studio assets to Google Drive", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                        }
                    }
                    OutlinedButton(
                        onClick = onOpenDrive,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("vault_open_drive_backup_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KikiCyan)
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = KikiCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Backup (.zip)", color = KikiCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Creator Stats & Sparks Balance
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenRewardsDashboard)
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = KikiSurfaceElevated
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CREATOR BALANCE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = KikiGold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = KikiGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${profile.sparksBalance} SPARKS",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                        color = KikiPearl
                                    )
                                }
                            }

                            HoloBadge(text = "VIP Tier 3")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenRewardsDashboard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_rewards_dashboard_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = KikiSurfaceElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = KikiGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Rewards & Activity Dashboard",
                            color = KikiPearl,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Dynamic Avatar Evolution & Collaborative Blend Suite
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiCyan.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                    .testTag("dynamic_avatar_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(avatarState.stage.auraColorHex))
                                    .padding(3.dp)
                                    .clip(CircleShape)
                                    .background(KikiDarkBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(avatarState.stage.badgeIcon, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = avatarState.evolutionRankTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                Text(
                                    text = "Aura: ${avatarState.stage.auraDescription}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = parseHexColor(avatarState.stage.auraColorHex)
                                )
                            }
                        }

                        HoloBadge(text = "Stage ${avatarState.stage.stageLevel}/5")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = KikiSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Equipped Trait:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiTextMuted
                                )
                                Text(
                                    text = avatarState.currentAccessory,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = KikiGold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (avatarState.nextStage != null) {
                                Text(
                                    text = "Next: ${avatarState.nextStage.title} (${(avatarState.progressPercent * 100).toInt()}% towards unlocking)",
                                    fontSize = 11.sp,
                                    color = KikiPearl
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { avatarState.progressPercent },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = KikiCyan,
                                    trackColor = KikiDarkBg
                                )
                            } else {
                                Text("Maximum Celestial Mastery Reached! 🌟", fontSize = 11.sp, color = KikiGold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Collaborative Blend Session button
                    Button(
                        onClick = { showBlendDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("launch_blend_session_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Live Collaborative Blend Session",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Sub-Tab Switcher
        item {
            TabRow(
                selectedTabIndex = selectedVaultTab,
                containerColor = KikiDarkBg,
                contentColor = KikiLilac,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedVaultTab]),
                        color = KikiLilac
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedVaultTab == index,
                        onClick = { selectedVaultTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedVaultTab == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedVaultTab == index) KikiPearl else KikiTextMuted
                            )
                        }
                    )
                }
            }
        }

        // TAB 0: Saved Formulations Deck
        if (selectedVaultTab == 0) {
            if (creations.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No saved formulas yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = KikiTextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onOpenLab,
                            colors = ButtonDefaults.buttonColors(containerColor = KikiLilac),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text("Launch Shade Blender", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(creations) { creation ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                    primaryHex = creation.primaryHex,
                                    secondaryHex = creation.secondaryHex,
                                    size = 40.dp
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = creation.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = KikiPearl
                                    )
                                    Text(
                                        text = "${creation.category.displayName} • ${creation.finish.displayName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = KikiCyan
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteCreation(creation.id) },
                                    modifier = Modifier.testTag("delete_${creation.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = KikiTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = creation.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HoloBadge(text = "Squalane: ${creation.squalanePercentage}%")
                                HoloBadge(text = "Shimmer: ${(creation.shimmerIntensity * 100).toInt()}%")
                                HoloBadge(text = creation.bottleShape)
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: Digital Charms & NFC Keys
        if (selectedVaultTab == 1) {
            items(charms) { charm ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            parseHexColor(charm.accentHex).copy(alpha = 0.5f),
                            RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(parseHexColor(charm.accentHex).copy(alpha = 0.15f))
                                .border(1.dp, parseHexColor(charm.accentHex), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Nfc,
                                contentDescription = null,
                                tint = parseHexColor(charm.accentHex),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = charm.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                HoloBadge(
                                    text = charm.rarity,
                                    containerColor = parseHexColor(charm.accentHex).copy(alpha = 0.2f),
                                    textColor = parseHexColor(charm.accentHex)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = charm.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "NFC Tag: ${charm.badgeNumber} • Unlocked ${charm.dateUnlocked}",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiCyan
                            )
                        }
                    }
                }
            }
        }

        // TAB 2: Daily Quests & Sparks Claimers
        if (selectedVaultTab == 2) {
            items(quests) { quest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (quest.isMindfulMoment) KikiGold.copy(alpha = 0.5f) else KikiSurfaceBorder,
                            RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (quest.isMindfulMoment) {
                            Image(
                                painter = painterResource(id = R.drawable.mindful_moments_asset_1793402969775),
                                contentDescription = "Mindful Moment",
                                modifier = Modifier.size(48.dp).padding(end = 10.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = quest.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = quest.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Progress: ${quest.currentCount}/${quest.targetCount} • +${quest.rewardSparks} Sparks",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiGold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        if (quest.claimed) {
                            HoloBadge(text = "Claimed ✓", containerColor = KikiSurfaceElevated)
                        } else {
                            Button(
                                onClick = { onClaimQuest(quest.id) },
                                enabled = quest.isCompleted,
                                modifier = Modifier
                                    .testTag("claim_${quest.id}")
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (quest.isCompleted) KikiGold else KikiSurfaceElevated,
                                    contentColor = if (quest.isCompleted) Color.Black else KikiTextMuted
                                ),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = if (quest.isCompleted) "Claim" else "${quest.currentCount}/${quest.targetCount}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBlendDialog) {
        CollaborativeBlendDialog(
            onDismiss = { showBlendDialog = false },
            onSaveSharedFormula = onSaveSharedFormula
        )
    }
}
