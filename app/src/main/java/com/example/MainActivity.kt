package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.util.configureAppCheck
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.GridView
import com.example.ui.components.OneDirectionWallpaper
import com.example.ui.components.OneDirectionRed
import com.example.ui.screens.OneDirectionHubScreen
import androidx.compose.material.icons.outlined.Diversity3
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HowToVote
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiSurfaceElevated
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthManager
import com.example.ui.components.DropDetailDialog
import com.example.ui.components.FormulaDetailDialog
import com.example.ui.components.IngredientGlossaryDialog
import com.example.ui.components.PrototypeOrderDialog
import com.example.ui.components.SparkBalancePill
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DropsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LabScreen
import com.example.ui.screens.RewardsDashboardScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.screens.ReikiGuideScreen
import com.example.ui.screens.StorefrontScreen
import com.example.ui.screens.MultiStoreDistributionScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.DriveScreen
import com.example.ui.screens.SyncSettingsScreen
import com.example.ui.screens.WeatherScreen
import com.example.ui.screens.LinkHubScreen
import com.example.ui.screens.NewsScreen
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiTextMuted
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.AiCompanionScreen
import com.example.ui.screens.TrendPredictorScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.OneDirectionDoorwaysScreen
import com.example.ui.screens.SmartWatchStudioScreen
import com.example.viewmodel.KikiNavTab
import com.example.viewmodel.KikiViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode != RESULT_OK) {
            // Update flow failed or was cancelled
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppContent()
            }
        }
        checkForUpdates()
    }

    private fun checkForUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    1
                )
            }
        }
    }
}

@Composable
fun AppContent(viewModel: KikiViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    val databaseId = remember {
        try {
            context.getString(R.string.firestore_database_id)
        } catch (e: Exception) {
            ""
        }
    }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            auth.currentUser?.let { user ->
                viewModel.handleAuthSuccess(user, databaseId)
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(databaseId) {
        if (databaseId.isNotBlank()) {
            viewModel.initFirestore(databaseId)
        }
        AuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { user ->
                currentUser = user
                viewModel.handleAuthSuccess(user, databaseId)
            },
            onUnauthenticated = { },
            scope = coroutineScope
        )
    }

    KikiWorldApp(
        viewModel = viewModel,
        currentUser = currentUser,
        databaseId = databaseId,
        credentialManager = credentialManager,
        onRequestSignIn = { }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KikiWorldApp(
    viewModel: KikiViewModel,
    currentUser: FirebaseUser?,
    databaseId: String,
    credentialManager: CredentialManager,
    onRequestSignIn: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentTab by viewModel.currentTab.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    val communityFormulas by viewModel.communityFormulas.collectAsState()
    val userCreations by viewModel.userCreations.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val charms by viewModel.charms.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val labState by viewModel.labState.collectAsState()
    val isOrderSampleOpen by viewModel.isOrderSampleOpen.collectAsState()
    val isIngredientGlossaryOpen by viewModel.isIngredientGlossaryOpen.collectAsState()
    val ingredientGlossary = viewModel.ingredientGlossary
    val isRewardsDashboardOpen by viewModel.isRewardsDashboardOpen.collectAsState()
    val activityHistory by viewModel.activityHistory.collectAsState()
    val activityTypeFilter by viewModel.activityTypeFilter.collectAsState()
    val tierMilestones = viewModel.tierMilestones

    val selectedDrop by viewModel.selectedDropDetail.collectAsState()
    val selectedFormula by viewModel.selectedFormulaDetail.collectAsState()
    val communityCategoryFilter by viewModel.communityCategoryFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val notificationMessage by viewModel.notificationMessage.collectAsState()
    val isOneDActive by viewModel.isOneDimensionalMode.collectAsState()
    val oneDirectionWallpaperActive by viewModel.oneDirectionWallpaperActive.collectAsState()
    val oneDirectionWallpaperDimming by viewModel.oneDirectionWallpaperDimming.collectAsState()
    val selectedWallpaperPresetId by viewModel.selectedWallpaperPresetId.collectAsState()
    val customWallpaperUri by viewModel.customWallpaperUri.collectAsState()
    val activeApp by viewModel.activeApp.collectAsState()

    LaunchedEffect(Unit) {
        val activity = context as? ComponentActivity
        if (activity?.intent?.component?.className?.contains("KikisWorldLauncher") == true) {
            viewModel.switchToApp(com.example.viewmodel.ActiveAppExperience.KIKIS_WORLD)
        }
    }

    var easternTimeText by remember {
        mutableStateOf(
            try {
                val zdt = java.time.ZonedDateTime.now(java.time.ZoneId.of("America/New_York"))
                zdt.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
            } catch (e: Exception) {
                "8:07 PM"
            }
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                val zdt = java.time.ZonedDateTime.now(java.time.ZoneId.of("America/New_York"))
                easternTimeText = zdt.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
            } catch (e: Exception) {
                // fallback
            }
            kotlinx.coroutines.delay(15000L)
        }
    }


    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(notificationMessage) {
        notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    val homeTabForActiveApp = if (activeApp == com.example.viewmodel.ActiveAppExperience.ONE_DIRECTION_WORLD) KikiNavTab.ONE_DIRECTION_DOORWAYS else KikiNavTab.DASHBOARD

    // Predictive back handler
    BackHandler(enabled = isIngredientGlossaryOpen || isRewardsDashboardOpen || selectedDrop != null || selectedFormula != null || isOrderSampleOpen || currentTab != homeTabForActiveApp) {
        when {
            isIngredientGlossaryOpen -> viewModel.closeIngredientGlossary()
            isRewardsDashboardOpen -> viewModel.closeRewardsDashboard()
            isOrderSampleOpen -> viewModel.closeOrderSampleDialog()
            selectedDrop != null -> viewModel.closeDropDetail()
            selectedFormula != null -> viewModel.closeFormulaDetail()
            currentTab != homeTabForActiveApp -> viewModel.selectTab(homeTabForActiveApp)
        }
    }

    val activeNavHub = when (currentTab) {
        KikiNavTab.ONE_DIRECTION_DOORWAYS, KikiNavTab.ONE_DIRECTION -> "1D"
        KikiNavTab.DASHBOARD, KikiNavTab.HOME, KikiNavTab.SYNC, KikiNavTab.WEATHER, KikiNavTab.NEWS -> "COMMAND"
        KikiNavTab.LAUNCHER -> "APPS"
        KikiNavTab.LAB, KikiNavTab.DROPS, KikiNavTab.AI_COMPANION, KikiNavTab.TREND_PREDICTOR -> "LAB"
        KikiNavTab.COMMUNITY, KikiNavTab.SOCIAL, KikiNavTab.WEB_PORTAL, KikiNavTab.PORTAL, KikiNavTab.KIKIVERSE -> "COMMUNITY"
        KikiNavTab.VAULT, KikiNavTab.PROFILE, KikiNavTab.JOURNAL, KikiNavTab.DRIVE, KikiNavTab.STORE, KikiNavTab.MULTI_STORE, KikiNavTab.WATCH_STUDIO -> "VAULT"
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDesktopWidescreen = maxWidth >= 840.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = KikiDarkBg,
            topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(OneDirectionRed, KikiLilac, KikiCyan)
                                    )
                                )
                                .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "1D×K",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "1D WORLD",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = " × ",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = KikiGold
                                )
                                Text(
                                    text = "KIKI WORLD",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = KikiCyan
                                )
                            }
                            Text(
                                text = "MERGED EDITION • AUTO-SYNC & AUTO-UPDATE ACTIVE ✓",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.4.sp
                                ),
                                color = KikiGold,
                                fontSize = 8.5.sp
                            )
                        }
                    }
                },
                actions = {
                    // Unified Merged Universe Theme & Focus Pill
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeApp == com.example.viewmodel.ActiveAppExperience.ONE_DIRECTION_WORLD) OneDirectionRed.copy(alpha = 0.8f) else KikiLilac.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable { viewModel.toggleAppExperience() }
                            .testTag("app_switcher_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "❤️ 1D + 💄 KIKI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable { viewModel.selectTab(KikiNavTab.WATCH_STUDIO) }
                            .testTag("open_watch_studio_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⌚", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Watch", fontSize = 10.sp, color = KikiPearl, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    SparkBalancePill(
                        balance = profile.sparksBalance,
                        onClick = { viewModel.openRewardsDashboard() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KikiDarkBg
                )
            )
        },
        bottomBar = {
            if (!isDesktopWidescreen) {
                NavigationBar(
                    containerColor = KikiSurfaceDark,
                    tonalElevation = 8.dp,
                    modifier = Modifier.border(0.5.dp, KikiSurfaceBorder)
                ) {
                    // Merged 1D World + Kiki World Unified 5-Tab Bottom Bar
                    NavigationBarItem(
                        selected = currentTab == KikiNavTab.ONE_DIRECTION_DOORWAYS,
                        onClick = { viewModel.selectTab(KikiNavTab.ONE_DIRECTION_DOORWAYS) },
                        icon = { Text("🚪", fontSize = 18.sp) },
                        label = { Text("Doorways", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color(0xFFFFD1DC).copy(alpha = 0.45f),
                            unselectedIconColor = KikiTextMuted,
                            unselectedTextColor = KikiTextMuted
                        ),
                        modifier = Modifier.testTag("nav_1d_doorways")
                    )

                    NavigationBarItem(
                        selected = currentTab == KikiNavTab.ONE_DIRECTION,
                        onClick = { viewModel.selectTab(KikiNavTab.ONE_DIRECTION) },
                        icon = { Text("❤️", fontSize = 18.sp) },
                        label = { Text("1D Hub", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OneDirectionRed,
                            selectedTextColor = Color.White,
                            indicatorColor = OneDirectionRed.copy(alpha = 0.35f),
                            unselectedIconColor = KikiTextMuted,
                            unselectedTextColor = KikiTextMuted
                        ),
                        modifier = Modifier.testTag("nav_one_direction")
                    )

                    NavigationBarItem(
                        selected = currentTab == KikiNavTab.DASHBOARD || currentTab == KikiNavTab.HOME,
                        onClick = { viewModel.selectTab(KikiNavTab.DASHBOARD) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == KikiNavTab.DASHBOARD) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Kiki Home"
                            )
                        },
                        label = { Text("Kiki Home", fontWeight = FontWeight.SemiBold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = KikiPearl,
                            indicatorColor = KikiLilac,
                            unselectedIconColor = KikiTextMuted,
                            unselectedTextColor = KikiTextMuted
                        ),
                        modifier = Modifier.testTag("nav_kiki_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == KikiNavTab.LAB,
                        onClick = { viewModel.selectTab(KikiNavTab.LAB) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == KikiNavTab.LAB) Icons.Filled.Science else Icons.Outlined.Science,
                                contentDescription = "Studio Lab"
                            )
                        },
                        label = { Text("Studio Lab", fontWeight = FontWeight.SemiBold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = KikiPearl,
                            indicatorColor = KikiLilac,
                            unselectedIconColor = KikiTextMuted,
                            unselectedTextColor = KikiTextMuted
                        ),
                        modifier = Modifier.testTag("nav_lab")
                    )

                    NavigationBarItem(
                        selected = currentTab == KikiNavTab.VAULT || currentTab == KikiNavTab.DRIVE || currentTab == KikiNavTab.MULTI_STORE,
                        onClick = { viewModel.selectTab(KikiNavTab.VAULT) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == KikiNavTab.VAULT) Icons.Filled.Stars else Icons.Outlined.Stars,
                                contentDescription = "Vault & Stores"
                            )
                        },
                        label = { Text("Vault", fontWeight = FontWeight.SemiBold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = KikiPearl,
                            indicatorColor = KikiCyan,
                            unselectedIconColor = KikiTextMuted,
                            unselectedTextColor = KikiTextMuted
                        ),
                        modifier = Modifier.testTag("nav_vault")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val navController = rememberNavController()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isDesktopWidescreen) {
                Windows11DesktopNavigationRail(
                    activeApp = activeApp,
                    currentTab = currentTab,
                    onSelectTab = { tab -> viewModel.selectTab(tab) },
                    onToggleApp = { viewModel.toggleAppExperience() },
                    onOpenWatchStudio = { viewModel.selectTab(KikiNavTab.WATCH_STUDIO) }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
            if (activeApp == com.example.viewmodel.ActiveAppExperience.ONE_DIRECTION_WORLD && oneDirectionWallpaperActive) {
                val activePresetUrl = com.example.viewmodel.default1DWallpaperPresets.find { it.id == selectedWallpaperPresetId }?.photoUrl
                OneDirectionWallpaper(
                    dimmingAlpha = oneDirectionWallpaperDimming,
                    customImageUri = customWallpaperUri,
                    presetImageUrl = activePresetUrl
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Unified Merged 1D World × Kiki World Quick-Navigation Ribbon
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KikiSurfaceDark.copy(alpha = 0.92f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val mergedQuickTabs = listOf(
                        Triple(KikiNavTab.ONE_DIRECTION_DOORWAYS, "🚪 1D Doorways", OneDirectionRed),
                        Triple(KikiNavTab.ONE_DIRECTION, "❤️ 1D Hub & Music", OneDirectionRed),
                        Triple(KikiNavTab.DASHBOARD, "🏠 Kiki World Home", KikiLilac),
                        Triple(KikiNavTab.LAB, "🧪 Clean Beauty Lab", KikiCyan),
                        Triple(KikiNavTab.DROPS, "🗳️ Drops", KikiGold),
                        Triple(KikiNavTab.DRIVE, "☁️ Google Drive & Flow", Color(0xFF4285F4)),
                        Triple(KikiNavTab.VAULT, "✨ Creator Vault", KikiLilac),
                        Triple(KikiNavTab.WATCH_STUDIO, "⌚ Smartwatch Studio", Color(0xFF00E676)),
                        Triple(KikiNavTab.MULTI_STORE, "🪟 PC & App Stores", Color(0xFF0078D4))
                    )
                    items(mergedQuickTabs.size) { index ->
                        val (tabItem, label, accentColor) = mergedQuickTabs[index]
                        val isSelected = currentTab == tabItem
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) accentColor.copy(alpha = 0.28f) else KikiSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) accentColor else KikiSurfaceBorder
                            ),
                            modifier = Modifier.clickable { viewModel.selectTab(tabItem) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else KikiPearl,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.fillMaxSize()
                    ) {
                composable("dashboard") {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "dashboardTabContent"
                    ) { tab ->
                        when (tab) {
                            KikiNavTab.ONE_DIRECTION_DOORWAYS -> OneDirectionDoorwaysScreen(
                                viewModel = viewModel
                            )

                            KikiNavTab.ONE_DIRECTION -> OneDirectionHubScreen(
                                viewModel = viewModel
                            )

                            KikiNavTab.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToLab = { viewModel.selectTab(KikiNavTab.LAB) },
                                onNavigateToWebPortal = { viewModel.selectTab(KikiNavTab.WEB_PORTAL) },
                                onNavigateToVault = { viewModel.selectTab(KikiNavTab.VAULT) }
                            )

                            KikiNavTab.HOME -> HomeScreen(
                                campaigns = campaigns,
                                trendingFormulas = communityFormulas,
                                profile = profile,
                                onOpenDropDetail = { viewModel.openDropDetail(it) },
                                onNavigateToLab = { 
                                    viewModel.selectTab(KikiNavTab.LAB)
                                    navController.navigate("shade_blender")
                                },
                                onNavigateToVault = { viewModel.selectTab(KikiNavTab.VAULT) },
                                onRemixFormula = { viewModel.remixFormula(it) }
                            )

                            KikiNavTab.DROPS -> DropsScreen(
                                campaigns = campaigns,
                                onVoteOption = { campaignId, optionId ->
                                    viewModel.voteOnCampaign(campaignId, optionId)
                                }
                            )

                            KikiNavTab.LAB -> {
                                // If tab state is LAB, ensure we navigate to shade_blender
                                LabScreen(
                                    state = labState,
                                    onCategoryChanged = { viewModel.updateLabCategory(it) },
                                    onNameChanged = { viewModel.updateLabName(it) },
                                    onPrimaryHexChanged = { viewModel.updateLabPrimaryHex(it) },
                                    onSecondaryHexChanged = { viewModel.updateLabSecondaryHex(it) },
                                    onFinishChanged = { viewModel.updateLabFinish(it) },
                                    onScentChanged = { viewModel.updateLabScent(it) },
                                    onBottleChanged = { viewModel.updateLabBottle(it) },
                                    onShimmerChanged = { viewModel.updateLabShimmer(it) },
                                    onSqualaneChanged = { viewModel.updateLabSqualane(it) },
                                    onNotesChanged = { viewModel.updateLabNotes(it) },
                                    onSaveFormula = { viewModel.saveCurrentLabFormula() },
                                    onOrderSampleClicked = { viewModel.openOrderSampleDialog() },
                                    onOpenGlossary = { viewModel.openIngredientGlossary() },
                                    onAiGenerateShade = { viewModel.aiGenerateShade(it) },
                                    onMetaMuseConsult = { viewModel.metaMuseConsult(it) },
                                    onSubmitToChallenge = { viewModel.submitToChallenge(it, "creationId") },
                                    onAnalyzeImage = { viewModel.analyzeImage(it) },
                                    challenges = challenges
                                )
                            }

                            KikiNavTab.COMMUNITY -> {
                                val spotlightEntry by viewModel.spotlightEntry.collectAsState()
                                CommunityScreen(
                                    formulas = communityFormulas,
                                    challenges = challenges,
                                    categoryFilter = communityCategoryFilter,
                                    searchQuery = searchQuery,
                                    onCategoryFilterChanged = { viewModel.setCommunityCategoryFilter(it) },
                                    onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                                    onRemixFormula = { viewModel.remixFormula(it) },
                                    onToggleLike = { viewModel.toggleLikeFormula(it) },
                                    onOpenFormulaDetail = { viewModel.openFormulaDetail(it) },
                                    spotlightEntry = spotlightEntry
                                )
                            }

                            KikiNavTab.VAULT -> VaultScreen(
                                profile = profile,
                                creations = userCreations,
                                quests = quests,
                                charms = charms,
                                currentUser = currentUser,
                                onGoogleSignInClicked = {
                                    if (currentUser == null) {
                                        AuthManager.onGoogleSignInClicked(
                                            context = context,
                                            credentialManager = credentialManager,
                                            onAuthSuccess = { user -> viewModel.handleAuthSuccess(user, databaseId) },
                                            onAuthError = { err -> /* handled */ },
                                            scope = coroutineScope
                                        )
                                    }
                                },
                                onSignOutClicked = {
                                    AuthManager.signOut(
                                        context = context,
                                        credentialManager = credentialManager,
                                        onSignOutComplete = {
                                            viewModel.handleSignOut()
                                            onRequestSignIn()
                                        },
                                        scope = coroutineScope
                                    )
                                },
                                onClaimQuest = { viewModel.claimQuest(it) },
                                onDeleteCreation = { viewModel.deleteCreation(it) },
                                onOpenLab = { 
                                    viewModel.selectTab(KikiNavTab.LAB)
                                    navController.navigate("shade_blender")
                                },
                                onOpenRewardsDashboard = { viewModel.openRewardsDashboard() },
                                onOpenDrive = { viewModel.selectTab(KikiNavTab.DRIVE) },
                                onSaveSharedFormula = { viewModel.saveCustomCreation(it) }
                            )

                            KikiNavTab.WEB_PORTAL -> {
                                val isSyncing by viewModel.isSyncingToRoom.collectAsState()
                                val selectedUrl by viewModel.selectedAiHubUrl.collectAsState()
                                WebPortalScreen(
                                    selectedServiceId = selectedUrl,
                                    isSyncing = isSyncing,
                                    onSyncTriggered = { viewModel.triggerRoomSync() },
                                    onServiceSelected = { viewModel.selectAiHubUrl(it) },
                                    onNavigateBack = { viewModel.selectAiHubUrl(null) }
                                )
                            }
                            KikiNavTab.STORE -> StorefrontScreen(
                                userSparksBalance = profile.sparksBalance,
                                onPurchaseSuccess = { product, sparksUsed ->
                                    viewModel.redeemStorePrototype(product.title, sparksUsed)
                                },
                                onFormulasExtracted = { list ->
                                    list.forEach { viewModel.saveCustomCreation(it) }
                                },
                                onOpenVault = { viewModel.selectTab(KikiNavTab.VAULT) },
                                onOpenDrive = { viewModel.selectTab(KikiNavTab.DRIVE) }
                            )
                            KikiNavTab.MULTI_STORE -> MultiStoreDistributionScreen(
                                onNavigateBack = { viewModel.selectTab(homeTabForActiveApp) },
                                onOpenWatchStudio = { viewModel.selectTab(KikiNavTab.WATCH_STUDIO) }
                            )
                            KikiNavTab.WATCH_STUDIO -> SmartWatchStudioScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.selectTab(homeTabForActiveApp) }
                            )
                            KikiNavTab.KIKIVERSE -> {
                                com.example.ui.screens.KikiVerseScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { viewModel.selectTab(KikiNavTab.DASHBOARD) }
                                )
                            }
                            KikiNavTab.LAUNCHER -> {
                                com.example.ui.screens.AppLauncherScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { viewModel.selectTab(KikiNavTab.DASHBOARD) }
                                )
                            }
                            KikiNavTab.PORTAL -> {
                                com.example.ui.screens.HubScreen(
                                    viewModel = viewModel
                                )
                            }
                            KikiNavTab.SOCIAL -> {
                                LinkHubScreen(viewModel = viewModel)
                            }
                            KikiNavTab.AI_COMPANION -> AiCompanionScreen(viewModel = viewModel)
                            KikiNavTab.TREND_PREDICTOR -> TrendPredictorScreen(viewModel = viewModel)
                            KikiNavTab.JOURNAL -> JournalScreen(viewModel = viewModel)
                            KikiNavTab.PROFILE -> ProfileScreen(viewModel = viewModel, onSignOut = {
                                AuthManager.signOut(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onSignOutComplete = {
                                        viewModel.handleSignOut()
                                        onRequestSignIn()
                                    },
                                    scope = coroutineScope
                                )
                            })
                            KikiNavTab.DRIVE -> DriveScreen(
                                onNavigateToLab = {
                                    viewModel.selectTab(KikiNavTab.LAB)
                                    navController.navigate("shade_blender")
                                },
                                onNavigateToVault = {
                                    viewModel.selectTab(KikiNavTab.VAULT)
                                }
                            )
                            KikiNavTab.SYNC -> SyncSettingsScreen(viewModel = viewModel)
                            KikiNavTab.WEATHER -> WeatherScreen()
                            KikiNavTab.NEWS -> NewsScreen()
                        }
                    }
                }

                composable("shade_blender") {
                    LabScreen(
                        state = labState,
                        onCategoryChanged = { viewModel.updateLabCategory(it) },
                        onNameChanged = { viewModel.updateLabName(it) },
                        onPrimaryHexChanged = { viewModel.updateLabPrimaryHex(it) },
                        onSecondaryHexChanged = { viewModel.updateLabSecondaryHex(it) },
                        onFinishChanged = { viewModel.updateLabFinish(it) },
                        onScentChanged = { viewModel.updateLabScent(it) },
                        onBottleChanged = { viewModel.updateLabBottle(it) },
                        onShimmerChanged = { viewModel.updateLabShimmer(it) },
                        onSqualaneChanged = { viewModel.updateLabSqualane(it) },
                        onNotesChanged = { viewModel.updateLabNotes(it) },
                        onSaveFormula = { viewModel.saveCurrentLabFormula() },
                        onOrderSampleClicked = { viewModel.openOrderSampleDialog() },
                        onOpenGlossary = { viewModel.openIngredientGlossary() },
                        onAiGenerateShade = { viewModel.aiGenerateShade(it) },
                        onMetaMuseConsult = { viewModel.metaMuseConsult(it) },
                        onSubmitToChallenge = { viewModel.submitToChallenge(it, "creationId") },
                        onAnalyzeImage = { viewModel.analyzeImage(it) },
                        challenges = challenges,
                        onOpenDrive = { viewModel.selectTab(KikiNavTab.DRIVE) }
                    )
                }

                composable("reiki_guide_screen") {
                    ReikiGuideScreen(onNavigateBack = { navController.popBackStack() })
                }
            }
        }
    }

            // Rewards & Activity History Dashboard Fullscreen Overlay
            if (isRewardsDashboardOpen) {
                RewardsDashboardScreen(
                    profile = profile,
                    activities = activityHistory,
                    milestones = tierMilestones,
                    selectedFilter = activityTypeFilter,
                    onFilterSelected = { viewModel.setActivityTypeFilter(it) },
                    onBackClicked = { viewModel.closeRewardsDashboard() }
                )
            }

            // Detail Dialogs
            selectedDrop?.let { drop ->
                DropDetailDialog(
                    campaign = drop,
                    onDismiss = { viewModel.closeDropDetail() },
                    onVoteOption = { campaignId, optionId ->
                        viewModel.voteOnCampaign(campaignId, optionId)
                    }
                )
            }

            selectedFormula?.let { formula ->
                FormulaDetailDialog(
                    formula = formula,
                    onDismiss = { viewModel.closeFormulaDetail() },
                    onRemix = { viewModel.remixFormula(formula) }
                )
            }

            if (isOrderSampleOpen) {
                PrototypeOrderDialog(
                    labState = labState,
                    userSparks = profile.sparksBalance,
                    onDismiss = { viewModel.closeOrderSampleDialog() },
                    onConfirmOrder = { engravingText ->
                        viewModel.confirmOrderSample(engravingText)
                    }
                )
            }

            if (isIngredientGlossaryOpen) {
                IngredientGlossaryDialog(
                    ingredients = ingredientGlossary,
                    onDismiss = { viewModel.closeIngredientGlossary() }
                )
            }
        }
    }
}
}
}

/**
 * Windows 11 Desktop Navigation Rail layer for widescreen desktop windowing.
 * Uses Material 3 NavigationRail with Fluent styling, app switching, and desktop tab routing.
 */
@Composable
fun Windows11DesktopNavigationRail(
    activeApp: com.example.viewmodel.ActiveAppExperience,
    currentTab: KikiNavTab,
    onSelectTab: (KikiNavTab) -> Unit,
    onToggleApp: () -> Unit,
    onOpenWatchStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier
            .fillMaxHeight()
            .width(108.dp)
            .border(0.5.dp, KikiSurfaceBorder),
        containerColor = KikiSurfaceDark,
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            ) {
                // Windows 11 Fluent App Switcher Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeApp == com.example.viewmodel.ActiveAppExperience.ONE_DIRECTION_WORLD) OneDirectionRed else Color(0xFF0078D4),
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { onToggleApp() }
                        .testTag("win11_app_switch_rail")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (activeApp == com.example.viewmodel.ActiveAppExperience.ONE_DIRECTION_WORLD) "1D" else "K",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (activeApp == com.example.viewmodel.ActiveAppExperience.ONE_DIRECTION_WORLD) "1D World" else "Kiki World",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = KikiPearl
                )
            }
        }
    ) {
        // Unified Merged 1D World × Kiki World Desktop Rail
        NavigationRailItem(
            selected = currentTab == KikiNavTab.ONE_DIRECTION_DOORWAYS,
            onClick = { onSelectTab(KikiNavTab.ONE_DIRECTION_DOORWAYS) },
            icon = { Text("🚪", fontSize = 20.sp) },
            label = { Text("Doorways", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color(0xFFFFD1DC).copy(alpha = 0.45f),
                unselectedIconColor = KikiTextMuted,
                unselectedTextColor = KikiTextMuted
            ),
            modifier = Modifier.testTag("rail_1d_doorways")
        )

        NavigationRailItem(
            selected = currentTab == KikiNavTab.ONE_DIRECTION,
            onClick = { onSelectTab(KikiNavTab.ONE_DIRECTION) },
            icon = { Text("❤️", fontSize = 20.sp) },
            label = { Text("1D Hub", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = OneDirectionRed,
                selectedTextColor = Color.White,
                indicatorColor = OneDirectionRed.copy(alpha = 0.35f),
                unselectedIconColor = KikiTextMuted,
                unselectedTextColor = KikiTextMuted
            ),
            modifier = Modifier.testTag("rail_one_direction")
        )

        NavigationRailItem(
            selected = currentTab == KikiNavTab.DASHBOARD || currentTab == KikiNavTab.HOME,
            onClick = { onSelectTab(KikiNavTab.DASHBOARD) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Kiki Home") },
            label = { Text("Kiki Home", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = KikiPearl,
                indicatorColor = KikiLilac,
                unselectedIconColor = KikiTextMuted,
                unselectedTextColor = KikiTextMuted
            ),
            modifier = Modifier.testTag("rail_kiki_home")
        )

        NavigationRailItem(
            selected = currentTab == KikiNavTab.LAB,
            onClick = { onSelectTab(KikiNavTab.LAB) },
            icon = { Icon(Icons.Filled.Science, contentDescription = "Lab") },
            label = { Text("Studio Lab", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = KikiPearl,
                indicatorColor = KikiLilac,
                unselectedIconColor = KikiTextMuted,
                unselectedTextColor = KikiTextMuted
            ),
            modifier = Modifier.testTag("rail_lab")
        )

        NavigationRailItem(
            selected = currentTab == KikiNavTab.VAULT || currentTab == KikiNavTab.DRIVE,
            onClick = { onSelectTab(KikiNavTab.VAULT) },
            icon = { Icon(Icons.Filled.Stars, contentDescription = "Vault") },
            label = { Text("Vault", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = KikiPearl,
                indicatorColor = KikiLilac,
                unselectedIconColor = KikiTextMuted,
                unselectedTextColor = KikiTextMuted
            ),
            modifier = Modifier.testTag("rail_vault")
        )

        NavigationRailItem(
            selected = currentTab == KikiNavTab.MULTI_STORE,
            onClick = { onSelectTab(KikiNavTab.MULTI_STORE) },
            icon = { Text("🪟", fontSize = 20.sp) },
            label = { Text("PC Stores", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = KikiPearl,
                indicatorColor = Color(0xFF0078D4),
                unselectedIconColor = KikiTextMuted,
                unselectedTextColor = KikiTextMuted
            ),
            modifier = Modifier.testTag("rail_pc_stores")
        )
    }
}
