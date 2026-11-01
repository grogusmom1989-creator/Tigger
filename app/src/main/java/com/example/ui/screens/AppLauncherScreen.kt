package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.example.util.DeviceSpecs
import com.example.util.DeviceSystemManager
import com.example.util.EcosystemFeatureItem
import com.example.util.EcosystemFeatures
import com.example.util.InstalledAppItem
import com.example.util.SystemToolItem
import com.example.viewmodel.KikiViewModel

enum class LauncherSubTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SYSTEM_TOOLS("System Tools", Icons.Default.Build),
    APP_BLENDER("App Blender", Icons.Default.Science),
    ALL_FEATURES("All 35+ Features", Icons.Default.AutoAwesome),
    INSTALLED_APPS("All Apps", Icons.Default.Apps),
    DEVICE_SPECS("Device Specs", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLauncherScreen(
    viewModel: KikiViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(LauncherSubTab.SYSTEM_TOOLS) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val systemTools = remember { DeviceSystemManager.getSystemTools(context) }
    val installedApps = remember { DeviceSystemManager.getInstalledApps(context) }
    val deviceSpecs = remember { DeviceSystemManager.getDeviceSpecs(context) }
    val blendedSuites by viewModel.blendedAppSuites.collectAsState()
    val allFeatures = remember { EcosystemFeatures.getAllFeatures() }
    var selectedFeatureCategory by remember { mutableStateOf("All (35+)") }

    val categories = remember(systemTools) {
        listOf("All") + systemTools.map { it.category }.distinct()
    }

    Scaffold(
        containerColor = KikiDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "KIKIOS DEVICE STATION",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = KikiPearl
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = KikiLilac.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, KikiLilac.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "PHONE & TABLET",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KikiLilac,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${deviceSpecs.deviceName} • ${deviceSpecs.batteryPct}% ${if (deviceSpecs.isCharging) "⚡" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiTextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("launcher_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KikiPearl)
                    }
                },
                actions = {
                    // Quick Google Play Store Action
                    FilledTonalButton(
                        onClick = { DeviceSystemManager.openGooglePlay(context) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF01875F).copy(alpha = 0.3f),
                            contentColor = Color(0xFF80E8B6)
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("play_store_quick_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shop,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Play Store", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KikiDarkBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Google Play Store Featured Banner
            GooglePlayHeroBanner(
                onOpenPlay = {
                    viewModel.recordAppLaunch("Google Play Store")
                    DeviceSystemManager.openGooglePlay(context)
                }
            )

            // Sub Tab Navigation
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = KikiSurfaceDark,
                contentColor = KikiPearl,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = KikiLilac,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                LauncherSubTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(tab.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(tab.label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        },
                        selectedContentColor = KikiLilac,
                        unselectedContentColor = KikiTextMuted
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                LauncherSubTab.SYSTEM_TOOLS -> {
                    SystemToolsTabContent(
                        systemTools = systemTools,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        searchQuery = searchQuery,
                        onSearchChanged = { searchQuery = it },
                        onToolClick = { tool ->
                            viewModel.recordAppLaunch(tool.title)
                            tool.launchAction(context)
                        }
                    )
                }

                LauncherSubTab.APP_BLENDER -> {
                    AppBlenderTabContent(
                        systemTools = systemTools,
                        blendedSuites = blendedSuites,
                        onBlendApps = { ids, names, customName ->
                            viewModel.createBlendedAppSuite(ids, names, customName)
                        },
                        onDeleteSuite = { suiteId ->
                            viewModel.deleteBlendedSuite(suiteId)
                        },
                        onLaunchTool = { tool ->
                            viewModel.recordAppLaunch(tool.title)
                            tool.launchAction(context)
                        }
                    )
                }

                LauncherSubTab.ALL_FEATURES -> {
                    AllFeaturesTabContent(
                        features = allFeatures,
                        categories = EcosystemFeatures.CATEGORIES,
                        selectedCategory = selectedFeatureCategory,
                        onCategorySelected = { selectedFeatureCategory = it },
                        searchQuery = searchQuery,
                        onSearchChanged = { searchQuery = it },
                        onFeatureClick = { feature ->
                            viewModel.recordAppLaunch(feature.title)
                            if (feature.systemAction != null) {
                                feature.systemAction.invoke(context)
                            } else if (feature.targetTab != null) {
                                viewModel.selectTab(feature.targetTab)
                            }
                        }
                    )
                }

                LauncherSubTab.INSTALLED_APPS -> {
                    InstalledAppsTabContent(
                        installedApps = installedApps,
                        searchQuery = searchQuery,
                        onSearchChanged = { searchQuery = it },
                        onAppClick = { app ->
                            viewModel.recordAppLaunch(app.name)
                            DeviceSystemManager.launchAppByPackage(context, app.packageName)
                        },
                        onOpenGooglePlay = {
                            viewModel.recordAppLaunch("Google Play Store")
                            DeviceSystemManager.openGooglePlay(context)
                        }
                    )
                }

                LauncherSubTab.DEVICE_SPECS -> {
                    DeviceDiagnosticsTabContent(
                        specs = deviceSpecs,
                        onOpenSettings = {
                            viewModel.recordAppLaunch("Device Settings")
                            context.startActivity(android.content.Intent(android.provider.Settings.ACTION_SETTINGS))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GooglePlayHeroBanner(onOpenPlay: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("google_play_hero_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onOpenPlay
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
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF01875F))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shop,
                        contentDescription = "Google Play",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Google Play Store",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF01875F),
                            modifier = Modifier.size(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "Direct shortcut to discover, install & update phone & tablet apps",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB5E4D1),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onOpenPlay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF01875F),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Open", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun SystemToolsTabContent(
    systemTools: List<SystemToolItem>,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onToolClick: (SystemToolItem) -> Unit
) {
    val filtered = remember(systemTools, selectedCategory, searchQuery) {
        systemTools.filter { tool ->
            val matchCategory = selectedCategory == "All" || tool.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    tool.title.contains(searchQuery, ignoreCase = true) ||
                    tool.description.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("Search tools (Camera, Settings, Wi-Fi, Calculator...)", color = KikiTextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KikiLilac) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("tools_search_field"),
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
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(5).forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategorySelected(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KikiLilac,
                            selectedLabelColor = Color.Black,
                            containerColor = KikiSurfaceDark,
                            labelColor = KikiPearl
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == cat,
                            borderColor = KikiSurfaceBorder,
                            selectedBorderColor = KikiLilac
                        )
                    )
                }
            }
        }

        items(filtered) { tool ->
            SystemToolCard(tool = tool, onClick = { onToolClick(tool) })
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SystemToolCard(
    tool: SystemToolItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tool_card_${tool.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, KikiSurfaceBorder),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (tool.isGooglePlay) Color(0xFF01875F).copy(alpha = 0.2f) else KikiLilac.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (tool.isGooglePlay) Color(0xFF01875F).copy(alpha = 0.5f) else KikiLilac.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.title,
                    tint = if (tool.isGooglePlay) Color(0xFF80E8B6) else KikiLilac,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tool.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                    tool.badge?.let { badgeText ->
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF01875F),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = KikiTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalIconButton(
                onClick = onClick,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = KikiDarkBg,
                    contentColor = KikiPearl
                )
            ) {
                Icon(Icons.Default.Launch, contentDescription = "Launch", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun InstalledAppsTabContent(
    installedApps: List<InstalledAppItem>,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onAppClick: (InstalledAppItem) -> Unit,
    onOpenGooglePlay: () -> Unit
) {
    val filtered = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("Search installed apps & games...", color = KikiTextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KikiLilac) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("apps_search_field"),
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
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISCOVERED APPS (${filtered.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = KikiLilac
                )
                TextButton(onClick = onOpenGooglePlay) {
                    Icon(Icons.Default.Shop, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF80E8B6))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Get More on Google Play", fontSize = 11.sp, color = Color(0xFF80E8B6), fontWeight = FontWeight.Bold)
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Apps, contentDescription = null, tint = KikiTextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No apps found matching \"$searchQuery\"" else "No third-party apps discovered",
                            color = KikiPearl,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Download your favorite apps and games from Google Play to launch them right here.",
                            color = KikiTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onOpenGooglePlay,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875F))
                        ) {
                            Icon(Icons.Default.Shop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Google Play Store")
                        }
                    }
                }
            }
        } else {
            items(filtered) { app ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, KikiSurfaceBorder),
                    onClick = { onAppClick(app) }
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = app.name,
                                color = KikiPearl,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        supportingContent = {
                            Text(
                                text = app.packageName,
                                color = KikiTextMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingContent = {
                            AppIconPreview(drawable = app.icon, appName = app.name)
                        },
                        trailingContent = {
                            Button(
                                onClick = { onAppClick(app) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = KikiLilac,
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Launch", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AppIconPreview(drawable: Drawable?, appName: String) {
    if (drawable != null) {
        val bitmap = remember(drawable) {
            val bmp = Bitmap.createBitmap(
                if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 48,
                if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 48,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bmp)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bmp
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = appName,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(HolographicGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = appName.take(1).uppercase(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DeviceDiagnosticsTabContent(
    specs: DeviceSpecs,
    onOpenSettings: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "KIKIOS DEVICE TELEMETRY",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = KikiPearl
            )
            Text(
                text = "Real-time specs and hardware status of your phone/tablet",
                style = MaterialTheme.typography.bodySmall,
                color = KikiTextMuted
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DiagnosticRow(label = "Device Hardware", value = specs.deviceName)
                    HorizontalDivider(color = KikiSurfaceBorder)
                    DiagnosticRow(label = "Operating System", value = specs.osVersion)
                    HorizontalDivider(color = KikiSurfaceBorder)
                    DiagnosticRow(
                        label = "Battery Level",
                        value = "${specs.batteryPct}% ${if (specs.isCharging) "(Charging ⚡)" else "(Discharging)"}"
                    )
                    HorizontalDivider(color = KikiSurfaceBorder)
                    DiagnosticRow(label = "Network Status", value = specs.networkStatus)
                    HorizontalDivider(color = KikiSurfaceBorder)
                    DiagnosticRow(label = "Internal Storage", value = "${specs.availableStorage} free of ${specs.totalStorage}")
                    HorizontalDivider(color = KikiSurfaceBorder)
                    DiagnosticRow(label = "System RAM", value = "${specs.availableRam} free of ${specs.totalRam}")
                }
            }
        }

        item {
            Button(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikiLilac, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.Build, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Complete Android Settings", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = KikiTextMuted)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = KikiPearl)
    }
}

@Composable
fun AppBlenderTabContent(
    systemTools: List<SystemToolItem>,
    blendedSuites: List<com.example.data.BlendedAppSuite>,
    onBlendApps: (List<String>, List<String>, String?) -> Unit,
    onDeleteSuite: (String) -> Unit,
    onLaunchTool: (SystemToolItem) -> Unit
) {
    val context = LocalContext.current
    var selectedToolIds by remember { mutableStateOf(setOf<String>()) }
    var customBlendName by remember { mutableStateOf("") }

    val selectedTools = remember(selectedToolIds, systemTools) {
        systemTools.filter { it.id in selectedToolIds }
    }

    val autoSuggestedName = remember(selectedTools) {
        if (selectedTools.size >= 2) {
            val p1 = selectedTools[0].title.split(" ").first()
            val p2 = selectedTools[1].title.split(" ").first()
            "${p1}${p2} Studio"
        } else ""
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiLilac.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HolographicGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "KIKIOS APP BLENDER",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = KikiPearl
                            )
                            Text(
                                text = "Blend multiple phone & tablet apps into unified hybrid suites",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )
                        }
                    }

                    Text(
                        text = "1. SELECT 2 OR MORE APPS TO BLEND:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = KikiLilac
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        systemTools.take(8).forEach { tool ->
                            val isSelected = tool.id in selectedToolIds
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedToolIds = if (isSelected) {
                                        selectedToolIds - tool.id
                                    } else {
                                        selectedToolIds + tool.id
                                    }
                                },
                                label = { Text(tool.title, fontSize = 11.sp) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                    } else {
                                        Icon(tool.icon, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = KikiLilac,
                                    selectedLabelColor = Color.Black,
                                    containerColor = KikiDarkBg,
                                    labelColor = KikiPearl
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        systemTools.drop(8).forEach { tool ->
                            val isSelected = tool.id in selectedToolIds
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedToolIds = if (isSelected) {
                                        selectedToolIds - tool.id
                                    } else {
                                        selectedToolIds + tool.id
                                    }
                                },
                                label = { Text(tool.title, fontSize = 11.sp) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                    } else {
                                        Icon(tool.icon, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = KikiLilac,
                                    selectedLabelColor = Color.Black,
                                    containerColor = KikiDarkBg,
                                    labelColor = KikiPearl
                                )
                            )
                        }
                    }

                    if (selectedTools.size >= 2) {
                        HorizontalDivider(color = KikiSurfaceBorder)
                        Text(
                            text = "2. CUSTOMIZE BLENDED SUITE NAME:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiLilac
                        )

                        OutlinedTextField(
                            value = if (customBlendName.isNotBlank()) customBlendName else autoSuggestedName,
                            onValueChange = { customBlendName = it },
                            label = { Text("Fused Suite Name") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val fusions = listOf("Nova", "Glaze", "Flux", "Nexus", "Sphere", "Pulse", "Link", "Sync")
                                    val p1 = selectedTools[0].title.split(" ").first()
                                    val p2 = selectedTools[1].title.split(" ").first()
                                    customBlendName = "${p1}${p2} ${fusions.random()}"
                                }) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Surprise blend name", tint = KikiLilac)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = KikiDarkBg,
                                unfocusedContainerColor = KikiDarkBg,
                                focusedBorderColor = KikiLilac,
                                unfocusedBorderColor = KikiSurfaceBorder,
                                focusedTextColor = KikiPearl,
                                unfocusedTextColor = KikiPearl
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                val finalName = if (customBlendName.isNotBlank()) customBlendName else autoSuggestedName
                                onBlendApps(
                                    selectedTools.map { it.id },
                                    selectedTools.map { it.title },
                                    finalName
                                )
                                selectedToolIds = emptySet()
                                customBlendName = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KikiLilac,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("🧪 Fuse Apps Into New Suite (+50 Sparks)", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "👉 Select 2 or more apps above to generate a custom blend!",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiTextMuted
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "ACTIVE BLENDED SUITES (${blendedSuites.size})",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = KikiLilac
            )
        }

        items(blendedSuites) { suite ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HolographicGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
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
                        }
                        IconButton(onClick = { onDeleteSuite(suite.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Delete blend", tint = KikiTextMuted, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Combined App Chips with Launch buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suite.appNames.forEach { appName ->
                            val matchingTool = systemTools.find { it.title.contains(appName, ignoreCase = true) || appName.contains(it.title, ignoreCase = true) }
                            FilledTonalButton(
                                onClick = {
                                    if (matchingTool != null) {
                                        onLaunchTool(matchingTool)
                                    } else {
                                        DeviceSystemManager.openGooglePlay(context)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = KikiDarkBg,
                                    contentColor = KikiPearl
                                )
                            ) {
                                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(appName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AllFeaturesTabContent(
    features: List<EcosystemFeatureItem>,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onFeatureClick: (EcosystemFeatureItem) -> Unit
) {
    val filtered = remember(features, selectedCategory, searchQuery) {
        features.filter { feature ->
            val matchesCategory = selectedCategory == "All (35+)" || feature.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                feature.title.contains(searchQuery, ignoreCase = true) ||
                feature.description.contains(searchQuery, ignoreCase = true) ||
                feature.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("Filter all 35+ features & tools...", color = KikiTextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KikiLilac) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = KikiTextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("all_features_search_field"),
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
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategorySelected(cat) },
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

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE FEATURES (${filtered.size})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiLilac
                )
                Text(
                    text = "Tap to launch or open",
                    style = MaterialTheme.typography.labelSmall,
                    color = KikiTextMuted
                )
            }
        }

        items(filtered) { feature ->
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
                    .testTag("all_feature_tile_${feature.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder),
                onClick = { onFeatureClick(feature) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = feature.icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = feature.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
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
                        onClick = { onFeatureClick(feature) },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
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

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


