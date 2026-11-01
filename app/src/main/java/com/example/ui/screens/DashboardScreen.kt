package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiTextMuted
import com.example.util.DeviceSystemManager
import com.example.util.EcosystemFeatureItem
import com.example.util.EcosystemFeatures
import com.example.viewmodel.KikiNavTab
import com.example.viewmodel.KikiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: KikiViewModel,
    onNavigateToLab: () -> Unit,
    onNavigateToWebPortal: () -> Unit,
    onNavigateToVault: () -> Unit
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val deviceSpecs = remember { DeviceSystemManager.getDeviceSpecs(context) }
    val systemTools = remember { DeviceSystemManager.getSystemTools(context) }
    val blendedSuites by viewModel.blendedAppSuites.collectAsState()
    val allFeatures = remember { EcosystemFeatures.getAllFeatures() }
    var selectedFeatureCategory by remember { mutableStateOf("All (35+)") }
    var isAllFeaturesExpanded by remember { mutableStateOf(true) }

    val searchMatches = remember(searchQuery, allFeatures) {
        if (searchQuery.isBlank()) emptyList()
        else allFeatures.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val categoryFeatures = remember(selectedFeatureCategory, allFeatures) {
        if (selectedFeatureCategory == "All (35+)") allFeatures
        else allFeatures.filter { it.category == selectedFeatureCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KikiDarkBg)
    ) {
        // Unified Header with Global System Search
        TopAppBar(
            title = {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search all 35+ tools, apps, AI & lab...", color = KikiTextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KikiLilac) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = KikiTextMuted)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("dashboard_search_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = KikiSurfaceDark,
                        unfocusedContainerColor = KikiSurfaceDark,
                        focusedBorderColor = KikiLilac,
                        unfocusedBorderColor = KikiSurfaceBorder,
                        focusedTextColor = KikiPearl,
                        unfocusedTextColor = KikiPearl
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            actions = {
                IconButton(
                    onClick = { viewModel.selectTab(KikiNavTab.SYNC) },
                    modifier = Modifier.testTag("dashboard_settings_button")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = KikiPearl)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = KikiDarkBg)
        )

        // Main Dashboard Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Search Results (shown whenever user types)
            if (searchQuery.isNotBlank()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SEARCH RESULTS (${searchMatches.size} FOUND)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF80E8B6)
                            )
                            TextButton(
                                onClick = { viewModel.setSearchQuery("") },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                            ) {
                                Text("Clear Search", fontSize = 11.sp, color = KikiLilac)
                            }
                        }

                        if (searchMatches.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No tools or features matching \"$searchQuery\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = KikiTextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Try searching: Camera, Blender, Drops, AI, Store, Weather, Journal...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = KikiLilac
                                    )
                                }
                            }
                        }
                    }
                }

                items(searchMatches) { feature ->
                    EcosystemFeatureCard(
                        feature = feature,
                        onLaunch = {
                            viewModel.recordAppLaunch(feature.title)
                            if (feature.systemAction != null) {
                                feature.systemAction.invoke(context)
                            } else if (feature.targetTab != null) {
                                viewModel.selectTab(feature.targetTab)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KIKIOS COMMAND CENTER",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = KikiPearl
                        )
                        Text(
                            text = "Unified OS hub for your phone & tablet",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiTextMuted
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = KikiSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, KikiSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = if (deviceSpecs.isCharging) Color(0xFF80E8B6) else KikiLilac,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${deviceSpecs.batteryPct}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KikiPearl
                            )
                        }
                    }
                }
            }

            // Google Play Store Shortcut Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_play_store_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    onClick = {
                        viewModel.recordAppLaunch("Google Play Store")
                        DeviceSystemManager.openGooglePlay(context)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0F382A),
                                        Color(0xFF164E3A),
                                        Color(0xFF1D3557)
                                    )
                                )
                            )
                            .border(1.dp, Color(0xFF01875F).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF01875F)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shop,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Google Play Store",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Direct shortcut to discover & update all apps & games",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFB5E4D1)
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.recordAppLaunch("Google Play Store")
                                    DeviceSystemManager.openGooglePlay(context)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875F)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Open", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            // Phone & Tablet Quick Tools Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PHONE & TABLET UTILITIES",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiLilac
                        )
                        TextButton(
                            onClick = { viewModel.selectTab(KikiNavTab.LAUNCHER) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(14.dp), tint = KikiLilac)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("All Apps & Tools →", fontSize = 11.sp, color = KikiLilac, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickToolTile(
                            title = "Camera",
                            icon = Icons.Default.CameraAlt,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.recordAppLaunch("Camera")
                                try {
                                    context.startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
                                } catch (_: Exception) {}
                            }
                        )
                        QuickToolTile(
                            title = "Photos",
                            icon = Icons.Default.PhotoLibrary,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.recordAppLaunch("Photos")
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply { type = "image/*" }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                        QuickToolTile(
                            title = "Settings",
                            icon = Icons.Default.Settings,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.recordAppLaunch("Settings")
                                try {
                                    context.startActivity(Intent(Settings.ACTION_SETTINGS))
                                } catch (_: Exception) {}
                            }
                        )
                        QuickToolTile(
                            title = "Calculator",
                            icon = Icons.Default.Calculate,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.recordAppLaunch("Calculator")
                                try {
                                    val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_CALCULATOR) }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickToolTile(
                            title = "Clock",
                            icon = Icons.Default.Schedule,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.recordAppLaunch("Clock")
                                try {
                                    context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS))
                                } catch (_: Exception) {}
                            }
                        )
                        QuickToolTile(
                            title = "Browser",
                            icon = Icons.Default.Language,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.recordAppLaunch("Web Browser")
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com")))
                                } catch (_: Exception) {}
                            }
                        )
                        QuickToolTile(
                            title = "Device Hub",
                            icon = Icons.Default.Apps,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTab(KikiNavTab.LAUNCHER) }
                        )
                        QuickToolTile(
                            title = "Sync",
                            icon = Icons.Default.Settings,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectTab(KikiNavTab.SYNC) }
                        )
                    }
                }
            }

            // Blended App Suites (Interface Fusion)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BLENDED APP SUITES (${blendedSuites.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiLilac
                    )
                    TextButton(
                        onClick = { viewModel.selectTab(KikiNavTab.LAUNCHER) },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(14.dp), tint = KikiLilac)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("App Blender →", fontSize = 11.sp, color = KikiLilac, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(blendedSuites.take(3)) { suite ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(HolographicGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = suite.blendedName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = suite.tagLine,
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.recordAppLaunch(suite.blendedName)
                                viewModel.selectTab(KikiNavTab.LAUNCHER)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KikiLilac, contentColor = Color.Black)
                        ) {
                            Text("Launch", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Kiki Core Modules
            item {
                Text(
                    text = "KIKIWORLD ECOSYSTEM",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = KikiLilac,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Featured Action Cards (Lab & Vault)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardActionButton(
                        title = "Studio Lab",
                        subtitle = "Synthesize formulas",
                        icon = Icons.Default.Science,
                        onClick = onNavigateToLab,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionButton(
                        title = "Beauty Vault",
                        subtitle = "Creations & perks",
                        icon = Icons.Default.Stars,
                        onClick = onNavigateToVault,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // AI Companion & Journal
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardActionButton(
                        title = "AI Companion",
                        subtitle = "Personal OS copilot",
                        icon = Icons.Default.AutoAwesome,
                        onClick = { viewModel.selectTab(KikiNavTab.AI_COMPANION) },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionButton(
                        title = "Daily Journal",
                        subtitle = "Reflect & log beauty",
                        icon = Icons.Default.Book,
                        onClick = { viewModel.selectTab(KikiNavTab.JOURNAL) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Content Engine & Community
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardActionButton(
                        title = "Content Engine",
                        subtitle = "PWA tools & AI Hub",
                        icon = Icons.Default.Language,
                        onClick = onNavigateToWebPortal,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionButton(
                        title = "Trends & Drops",
                        subtitle = "Seasonal predictions",
                        icon = Icons.Default.Stars,
                        onClick = { viewModel.selectTab(KikiNavTab.TREND_PREDICTOR) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ALL 35+ ECOSYSTEM FEATURES DIRECTORY (MASTER MATRIX)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ALL ECOSYSTEM FEATURES",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = KikiLilac
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = KikiLilac.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiLilac.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "${allFeatures.size} ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KikiLilac,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Instant one-tap launcher for all 35+ tools, modules & engines",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )
                        }
                        TextButton(
                            onClick = { isAllFeaturesExpanded = !isAllFeaturesExpanded },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = if (isAllFeaturesExpanded) "Hide ▲" else "View All ▼",
                                fontSize = 11.sp,
                                color = KikiLilac,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isAllFeaturesExpanded) {
                        // Category Horizontal Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            EcosystemFeatures.CATEGORIES.forEach { cat ->
                                FilterChip(
                                    selected = selectedFeatureCategory == cat,
                                    onClick = { selectedFeatureCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
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
                }
            }

            if (isAllFeaturesExpanded) {
                items(categoryFeatures) { feature ->
                    EcosystemFeatureCard(
                        feature = feature,
                        onLaunch = {
                            viewModel.recordAppLaunch(feature.title)
                            if (feature.systemAction != null) {
                                feature.systemAction.invoke(context)
                            } else if (feature.targetTab != null) {
                                viewModel.selectTab(feature.targetTab)
                            }
                        }
                    )
                }
            }

            // Hardware & OS Telemetry Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, KikiSurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DEVICE HARDWARE SPECS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiLilac
                            )
                            Text(
                                text = deviceSpecs.networkStatus,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF80E8B6)
                            )
                        }
                        Text(
                            text = "${deviceSpecs.deviceName} • ${deviceSpecs.osVersion}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Storage: ${deviceSpecs.availableStorage} Free",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )
                            Text(
                                text = "RAM: ${deviceSpecs.availableRam} Free",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickToolTile(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .testTag("quick_tool_${title.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, KikiSurfaceBorder),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = KikiLilac,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = KikiPearl,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DashboardActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(86.dp)
            .testTag("dashboard_btn_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, KikiSurfaceBorder),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(KikiLilac.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = KikiLilac,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiPearl
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = KikiTextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun EcosystemFeatureCard(
    feature: EcosystemFeatureItem,
    onLaunch: () -> Unit
) {
    val accentColor = remember(feature.accentHex) {
        try {
            Color(android.graphics.Color.parseColor(feature.accentHex))
        } catch (_: Exception) {
            KikiLilac
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("feature_card_${feature.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder),
        onClick = onLaunch
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiPearl,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = KikiTextMuted,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = feature.category.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onLaunch,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_launch_${feature.id}")
            ) {
                Text(
                    text = feature.actionLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Launch,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

