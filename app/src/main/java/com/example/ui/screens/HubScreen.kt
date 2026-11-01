package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceDark
import com.example.viewmodel.KikiNavTab
import com.example.viewmodel.KikiViewModel

data class PortalItem(val name: String, val icon: ImageVector, val category: String, val navTab: KikiNavTab?)

@Composable
fun HubScreen(viewModel: KikiViewModel) {
    val portals = listOf(
        PortalItem("Lab", Icons.Default.Science, "Apps", KikiNavTab.LAB),
        PortalItem("Vault", Icons.Default.Stars, "Apps", KikiNavTab.VAULT),
        PortalItem("Store", Icons.Default.Store, "Apps", KikiNavTab.STORE),
        PortalItem("Games", Icons.Default.PlayArrow, "Games", KikiNavTab.LAUNCHER),
        PortalItem("Community", Icons.Default.Diversity3, "Feeds", KikiNavTab.COMMUNITY),
        PortalItem("PWA", Icons.Default.Public, "Apps", KikiNavTab.WEB_PORTAL),
        PortalItem("Backup", Icons.Default.Backup, "Apps", KikiNavTab.DRIVE),
        PortalItem("Sync", Icons.Default.Sync, "Apps", KikiNavTab.SYNC),
        PortalItem("Weather", Icons.Default.Cloud, "Feeds", KikiNavTab.WEATHER),
        PortalItem("News", Icons.Default. Newspaper, "Feeds", KikiNavTab.NEWS),
        PortalItem("Dashboard", Icons.Default.Home, "Feeds", KikiNavTab.DASHBOARD),
        PortalItem("Profile", Icons.Default.Person, "Account", KikiNavTab.PROFILE),
        PortalItem("Drops", Icons.Default.HowToVote, "Feeds", KikiNavTab.DROPS),
        PortalItem("Social", Icons.Default.Diversity3, "Feeds", KikiNavTab.SOCIAL)
    )

    Column(modifier = Modifier.fillMaxSize().background(KikiDarkBg).padding(16.dp)) {
        Text("PORTAL HUB", style = MaterialTheme.typography.headlineMedium, color = KikiPearl, modifier = Modifier.padding(bottom = 16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(portals) { item ->
                Card(
                    modifier = Modifier
                        .height(140.dp)
                        .testTag("hub_card_${item.name.lowercase().replace(" ", "_")}"),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                    onClick = { item.navTab?.let { viewModel.selectTab(it) } }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.name,
                                tint = KikiLilac,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiLilac.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = KikiPearl
                        )
                    }
                }
            }
        }
    }
}
