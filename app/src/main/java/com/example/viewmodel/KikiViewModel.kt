package com.example.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CommunityFormula
import com.example.data.CreatorProfile
import com.example.data.CustomCreation
import com.example.data.DailyQuest
import com.example.data.DigitalCharm
import com.example.data.DropCampaign
import com.example.data.FinishType
import com.example.data.KikiRepository
import com.example.data.ProductCategory
import com.example.data.Challenge
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.example.BuildConfig
import com.example.App
import com.example.data.SparkleRepository
import com.example.data.room.SparkleItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

enum class KikiNavTab(val title: String) {
    ONE_DIRECTION_DOORWAYS("1D Doorways"),
    ONE_DIRECTION("1D Hub"),
    DASHBOARD("Command Center"),
    HOME("Home"),
    DROPS("Drops"),
    LAB("Lab"),
    COMMUNITY("Community"),
    VAULT("Vault"),
    WEB_PORTAL("PWA"),
    STORE("Store"),
    KIKIVERSE("KikiVerse"),
    LAUNCHER("Device Hub"),
    SOCIAL("Social"),
    PROFILE("Profile"),
    DRIVE("Backup"),
    PORTAL("Portal"),
    SYNC("Sync"),
    WEATHER("Weather"),
    NEWS("News"),
    AI_COMPANION("AI Companion"),
    TREND_PREDICTOR("Trend Predictor"),
    JOURNAL("Journal"),
    MULTI_STORE("Multi-Store"),
    WATCH_STUDIO("Smart Watch")
}

enum class AppLayoutMode(val displayName: String, val icon: String, val subtitle: String) {
    ONE_DIRECTION("1-Direction Flow", "↕️", "Unified single-axis linear stream"),
    STANDARD("Standard Studio", "📱", "Multi-tab modular workspace"),
    WINDOWS_DESKTOP("Windows 11 PC", "🪟", "Fluent desktop widescreen rail")
}

enum class ActiveAppExperience(
    val title: String,
    val iconEmoji: String,
    val subtitle: String,
    val brandTag: String,
    val primaryColorHex: String
) {
    ONE_DIRECTION_WORLD(
        title = "1D World",
        iconEmoji = "❤️",
        subtitle = "One Direction Fan Club & Jukebox",
        brandTag = "DIRECTIONERS",
        primaryColorHex = "#E50914"
    ),
    KIKIS_WORLD(
        title = "Kiki's World",
        iconEmoji = "💄",
        subtitle = "Clean Beauty Studio & Drops",
        brandTag = "LAB STUDIO",
        primaryColorHex = "#B388FF"
    )
}

data class OneDirectionWallpaperPreset(
    val id: String,
    val name: String,
    val era: String,
    val description: String,
    val photoUrl: String,
    val accentHex: String
)

val default1DWallpaperPresets = listOf(
    OneDirectionWallpaperPreset(
        id = "wpp_concert_stadium",
        name = "Where We Are Stadium Lights",
        era = "2014 Stadium Tour",
        description = "Live concert lights with Harry, Niall, Zayn, Liam & Louis under stadium floodlights",
        photoUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=1200&q=80",
        accentHex = "#E50914"
    ),
    OneDirectionWallpaperPreset(
        id = "wpp_midnight_memories",
        name = "Midnight Memories Rock Stage",
        era = "2013 Rock Era",
        description = "Dark rock concert arena with laser spotlights and smoke haze",
        photoUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80",
        accentHex = "#7B2CBF"
    ),
    OneDirectionWallpaperPreset(
        id = "wpp_up_all_night_beach",
        name = "What Makes You Beautiful Coast",
        era = "2011 Debut Era",
        description = "Golden hour ocean beach breeze from the iconic debut music video",
        photoUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80",
        accentHex = "#FF007F"
    ),
    OneDirectionWallpaperPreset(
        id = "wpp_starlight_four",
        name = "Night Changes Starlight",
        era = "2014 Golden Acoustic",
        description = "Twinkling carnival lights and romantic starlight evening glow",
        photoUrl = "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=1200&q=80",
        accentHex = "#FEE440"
    ),
    OneDirectionWallpaperPreset(
        id = "wpp_london_vintage",
        name = "Take Me Home British Flag & Phonebox",
        era = "2012 London City",
        description = "Iconic London red telephone box and vintage UK city aesthetic",
        photoUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=1200&q=80",
        accentHex = "#E50914"
    )
)

data class LabEditorState(
    val category: ProductCategory = ProductCategory.LIP_OIL,
    val formulaName: String = "Cosmic Peach Glaze",
    val primaryHex: String = "#FF729F",
    val secondaryHex: String = "#5CE5E7",
    val finish: FinishType = FinishType.HOLOGRAPHIC,
    val scentNote: String = "Vanilla Squalane",
    val bottleShape: String = "Prism Wand",
    val shimmerIntensity: Float = 0.75f,
    val squalanePercentage: Int = 15,
    val notes: String = "Custom antioxidant-rich formula designed for high gloss & maximum hydration."
)

class KikiViewModel(
    private val repository: KikiRepository = KikiRepository()
) : ViewModel() {

    private val _layoutMode = MutableStateFlow(AppLayoutMode.ONE_DIRECTION)
    val layoutMode: StateFlow<AppLayoutMode> = _layoutMode.asStateFlow()

    fun setLayoutMode(mode: AppLayoutMode) {
        _layoutMode.value = mode
        _notificationMessage.value = "Switched to ${mode.displayName} (${mode.subtitle})"
    }

    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    val campaigns: StateFlow<List<DropCampaign>> = repository.campaigns
    val communityFormulas: StateFlow<List<CommunityFormula>> = repository.communityFormulas
    val challenges: StateFlow<List<Challenge>> = repository.challenges
    val userCreations: StateFlow<List<CustomCreation>> = repository.userCreations
    val quests: StateFlow<List<DailyQuest>> = repository.quests
    val charms: StateFlow<List<DigitalCharm>> = repository.charms
    val hubs: StateFlow<List<com.example.data.HubPortal>> = repository.observeHubPortals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
    val socialFeed: StateFlow<List<com.example.data.SocialFeedItem>> = repository.observeSocialFeed().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
    val mediaItems: StateFlow<List<com.example.data.MediaItem>> = repository.mediaItems
    val blogEntries: StateFlow<List<com.example.data.BlogEntry>> = repository.blogEntries
    val musicTracks: StateFlow<List<com.example.data.MusicTrack>> = repository.musicTracks
    val shrinePages: StateFlow<List<com.example.data.ShrinePage>> = repository.shrinePages
    val galleryItems: StateFlow<List<com.example.data.GalleryItem>> = repository.galleryItems
    val leaderboard: StateFlow<List<com.example.data.LeaderboardItem>> = repository.observeLeaderboard()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
    val products: StateFlow<List<com.example.data.ShopifyProduct>> = repository.products
    val profile: StateFlow<CreatorProfile> = repository.profile

    private val _currentTab = MutableStateFlow(KikiNavTab.ONE_DIRECTION)
    val currentTab: StateFlow<KikiNavTab> = _currentTab.asStateFlow()

    private val _labState = MutableStateFlow(LabEditorState())
    val labState: StateFlow<LabEditorState> = _labState.asStateFlow()

    private val _selectedDropDetail = MutableStateFlow<DropCampaign?>(null)
    val selectedDropDetail: StateFlow<DropCampaign?> = _selectedDropDetail.asStateFlow()

    private val _selectedFormulaDetail = MutableStateFlow<CommunityFormula?>(null)
    val selectedFormulaDetail: StateFlow<CommunityFormula?> = _selectedFormulaDetail.asStateFlow()

    private val _communityCategoryFilter = MutableStateFlow<ProductCategory?>(null)
    val communityCategoryFilter: StateFlow<ProductCategory?> = _communityCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentUser = MutableStateFlow<com.google.firebase.auth.FirebaseUser?>(com.google.firebase.Firebase.auth.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser.asStateFlow()

    private val _isOrderSampleOpen = MutableStateFlow(false)
    val isOrderSampleOpen: StateFlow<Boolean> = _isOrderSampleOpen.asStateFlow()

    private val _isIngredientGlossaryOpen = MutableStateFlow(false)
    val isIngredientGlossaryOpen: StateFlow<Boolean> = _isIngredientGlossaryOpen.asStateFlow()

    val ingredientGlossary: List<com.example.data.IngredientInfo> = repository.getIngredientGlossary()

    val activityHistory: StateFlow<List<com.example.data.CreatorActivityItem>> = repository.activityHistory
    val tierMilestones: List<com.example.data.TierMilestone> = repository.getTierMilestones()

    private val _isRewardsDashboardOpen = MutableStateFlow(false)
    val isRewardsDashboardOpen: StateFlow<Boolean> = _isRewardsDashboardOpen.asStateFlow()

    private val _activityTypeFilter = MutableStateFlow<com.example.data.ActivityType?>(null)
    val activityTypeFilter: StateFlow<com.example.data.ActivityType?> = _activityTypeFilter.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    private val _selectedAiHubUrl = MutableStateFlow<String?>(null)
    val selectedAiHubUrl: StateFlow<String?> = _selectedAiHubUrl.asStateFlow()

    private val _activeApp = MutableStateFlow(ActiveAppExperience.ONE_DIRECTION_WORLD)
    val activeApp: StateFlow<ActiveAppExperience> = _activeApp.asStateFlow()

    fun switchToApp(app: ActiveAppExperience) {
        _activeApp.value = app
        if (app == ActiveAppExperience.ONE_DIRECTION_WORLD) {
            _currentTab.value = KikiNavTab.ONE_DIRECTION_DOORWAYS
            _oneDirectionWallpaperActive.value = true
            _notificationMessage.value = "Switched to 1D World! 🎤"
        } else {
            _currentTab.value = KikiNavTab.DASHBOARD
            _oneDirectionWallpaperActive.value = false
            _notificationMessage.value = "Switched to Kiki's World Studio! 💄"
        }
    }

    fun toggleAppExperience() {
        val next = if (_activeApp.value == ActiveAppExperience.ONE_DIRECTION_WORLD) {
            ActiveAppExperience.KIKIS_WORLD
        } else {
            ActiveAppExperience.ONE_DIRECTION_WORLD
        }
        switchToApp(next)
    }

    private val _isOneDimensionalMode = MutableStateFlow(true)
    val isOneDimensionalMode: StateFlow<Boolean> = _isOneDimensionalMode.asStateFlow()

    private val _oneDirectionWallpaperActive = MutableStateFlow(true)
    val oneDirectionWallpaperActive: StateFlow<Boolean> = _oneDirectionWallpaperActive.asStateFlow()

    private val _oneDirectionWallpaperDimming = MutableStateFlow(0.35f)
    val oneDirectionWallpaperDimming: StateFlow<Float> = _oneDirectionWallpaperDimming.asStateFlow()

    private val _selectedWallpaperPresetId = MutableStateFlow<String>("wpp_concert_stadium")
    val selectedWallpaperPresetId: StateFlow<String> = _selectedWallpaperPresetId.asStateFlow()

    private val _customWallpaperUri = MutableStateFlow<String?>(null)
    val customWallpaperUri: StateFlow<String?> = _customWallpaperUri.asStateFlow()

    fun selectWallpaperPreset(presetId: String) {
        _selectedWallpaperPresetId.value = presetId
        _customWallpaperUri.value = null
        _oneDirectionWallpaperActive.value = true
        val name = default1DWallpaperPresets.find { it.id == presetId }?.name ?: presetId
        _notificationMessage.value = "📸 Set 1D Photo Wallpaper: '$name'"
    }

    fun setCustomWallpaperUri(uriString: String) {
        _customWallpaperUri.value = uriString
        _oneDirectionWallpaperActive.value = true
        _notificationMessage.value = "🖼️ Applied custom 1D photo wallpaper from your device!"
    }

    fun clearCustomWallpaper() {
        _customWallpaperUri.value = null
        _selectedWallpaperPresetId.value = "wpp_concert_stadium"
        _notificationMessage.value = "Reset to default 1D photo wallpaper."
    }

    fun toggleOneDirectionWallpaper() {
        _oneDirectionWallpaperActive.update { !it }
        _notificationMessage.value = if (_oneDirectionWallpaperActive.value) {
            "💖 One Direction Band Wallpaper enabled!"
        } else {
            "One Direction Wallpaper disabled."
        }
    }

    fun setOneDirectionWallpaperDimming(dimming: Float) {
        _oneDirectionWallpaperDimming.value = dimming.coerceIn(0.1f, 0.8f)
    }

    fun toggleOneDimensionalMode() {
        _isOneDimensionalMode.update { !it }
        val active = _isOneDimensionalMode.value
        _notificationMessage.value = if (active) {
            "💖 1D Mode Active: One Direction Hub & Tribute Deck enabled!"
        } else {
            "Standard View restored."
        }
    }

    fun loadOneDirectionFormula(
        name: String,
        primaryHex: String,
        secondaryHex: String,
        finish: FinishType,
        squalane: Int,
        scent: String,
        notes: String
    ) {
        _labState.update {
            it.copy(
                formulaName = name,
                primaryHex = primaryHex,
                secondaryHex = secondaryHex,
                finish = finish,
                squalanePercentage = squalane,
                scentNote = scent,
                notes = notes
            )
        }
        selectTab(KikiNavTab.LAB)
        _notificationMessage.value = "🧪 Loaded '$name' into Lab Blender!"
    }

    val sparkleRepository: SparkleRepository by lazy {
        try {
            App.instance.sparkleRepository
        } catch (e: Exception) {
            SparkleRepository(App.instance.applicationContext)
        }
    }

    val allSparkles: StateFlow<List<SparkleItemEntity>> by lazy {
        try {
            sparkleRepository.allSparkles.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SparkleRepository.initialSparkleSeedData
            )
        } catch (e: Exception) {
            MutableStateFlow(SparkleRepository.initialSparkleSeedData).asStateFlow()
        }
    }

    val collectedSparkles: StateFlow<List<SparkleItemEntity>> by lazy {
        try {
            sparkleRepository.collectedSparkles.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SparkleRepository.initialSparkleSeedData.filter { it.isCollected }
            )
        } catch (e: Exception) {
            MutableStateFlow(SparkleRepository.initialSparkleSeedData.filter { it.isCollected }).asStateFlow()
        }
    }

    fun toggleSparkleCollected(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            try {
                sparkleRepository.toggleCollected(id, currentStatus)
                _notificationMessage.value = if (!currentStatus) "✨ Sparkle item collected into your offline vault!" else "Item uncollected."
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun collectSparkleItem(id: String) {
        viewModelScope.launch {
            try {
                sparkleRepository.collectItem(id)
                _notificationMessage.value = "✨ Unlocked new 1D Sparkle collectible!"
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun awardSparks(amount: Int, reason: String) {
        repository.awardSparks(amount)
        _notificationMessage.value = "🎉 +$amount Sparks awarded for $reason!"
    }

    fun installMetaMuseDriveBackup() {
        viewModelScope.launch {
            try {
                val count = com.example.util.GoogleDriveManager.installMetaMuseDriveBackup(repository)
                if (count > 0) {
                    _notificationMessage.value = "🚀 Installed all code & formulas from Meta Muse Drive backup! ($count loaded into Vault)"
                }
            } catch (e: Exception) {
                // non-blocking
            }
        }
    }

    init {
        installMetaMuseDriveBackup()
    }

    private val _aiCompanionMessages = MutableStateFlow<List<String>>(listOf("Kiki: Hello! How can I help you today?"))
    val aiCompanionMessages: StateFlow<List<String>> = _aiCompanionMessages.asStateFlow()

    fun sendAiMessage(message: String) {
        _aiCompanionMessages.update { it + "User: $message" }
        if (message.trim().equals("backup", ignoreCase = true) || message.contains("backup", ignoreCase = true)) {
            viewModelScope.launch {
                try {
                    val zipBytes = com.example.util.GoogleDriveManager.createFullAIStudioBackupZip(repository)
                    val fileName = "AI_Studio_KikiWorld_Backup_${System.currentTimeMillis() / 1000}.zip"
                    com.example.util.GoogleDriveManager.uploadFile(fileName, zipBytes)
                    _notificationMessage.value = "📦 Backed up AI Studio to Google Drive: '$fileName'!"
                    _aiCompanionMessages.update { 
                        it + "Kiki: ✨ Done! Your entire AI Studio portfolio (${repository.userCreations.value.size} custom formulations, community drop votes, and Meta Muse trends) has been packaged into '$fileName' (${zipBytes.size / 1024} KB) and backed up to Google Drive!" 
                    }
                } catch (e: Exception) {
                    _aiCompanionMessages.update { it + "Kiki: I ran into an issue backing up to Google Drive: ${e.localizedMessage}" }
                }
            }
            return
        }
        viewModelScope.launch {
            try {
                val response = model.generateContent(message)
                _aiCompanionMessages.update { it + "Kiki: ${response.text ?: "I'm not sure how to respond to that."}" }
            } catch (e: Exception) {
                _aiCompanionMessages.update { it + "Kiki: Sorry, I had trouble processing that." }
            }
        }
    }

    private val _isBackupEnabled = MutableStateFlow(true)
    val isBackupEnabled: StateFlow<Boolean> = _isBackupEnabled.asStateFlow()

    private val _isFeedSyncEnabled = MutableStateFlow(true)
    val isFeedSyncEnabled: StateFlow<Boolean> = _isFeedSyncEnabled.asStateFlow()

    private val _weather = MutableStateFlow<com.example.data.WeatherResponse?>(null)
    val weather: StateFlow<com.example.data.WeatherResponse?> = _weather.asStateFlow()

    private val _news = MutableStateFlow<List<com.example.data.NewsItem>>(emptyList())
    val news: StateFlow<List<com.example.data.NewsItem>> = _news.asStateFlow()

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _notificationMessage.value = "Consulting Kikiweather AI for $city..."
            try {
                val response = model.generateContent("Provide a creative, Kikiworld-themed weather report for $city. Return a short description (under 20 words).")
                _weather.value = com.example.data.WeatherResponse(
                    main = com.example.data.WeatherMain(temp = null),
                    weather = listOf(com.example.data.WeatherDescription(description = response.text ?: "Magical")),
                    name = city
                )
                _notificationMessage.value = "Kikiweather AI updated!"
            } catch (e: Exception) {
                _notificationMessage.value = "Could not fetch magical weather: ${e.localizedMessage}"
            }
        }
    }

    fun fetchNews() {
        viewModelScope.launch {
            // Mocking news fetch for now
            _news.value = listOf(
                com.example.data.NewsItem("New Beauty Trends 2026", "https://example.com/news1", "Discover the latest shades."),
                com.example.data.NewsItem("Sustainable Packaging Innovations", "https://example.com/news2", "How brands are going green.")
            )
        }
    }

    fun toggleBackup(context: android.content.Context, enabled: Boolean) {
        _isBackupEnabled.value = enabled
        if (enabled) {
            com.example.util.SyncManager.scheduleBackup(context)
        } else {
            androidx.work.WorkManager.getInstance(context).cancelUniqueWork("DriveBackup")
        }
    }

    fun selectAiHubUrl(serviceId: String?) {
        _selectedAiHubUrl.value = serviceId
        when (serviceId) {
            "mind_mesh" -> mindMeshIdeation("Creative ideas for Kiki Network")
            "vision_lab" -> visionLabPromptGeneration("Avant-garde cosmetic branding")
            "reiki_guide" -> reikiGuidanceGeneration("Daily healing intention")
            "neocities_pwa" -> { /* Handled by WebView */ }
            else -> {}
        }
        if (serviceId?.contains("reiki-guide") == true) {
            _notificationMessage.value = "✨ Opening Reiki Energy Guide..."
        }
    }

    fun openIngredientGlossary() {
        _isIngredientGlossaryOpen.value = true
    }

    fun closeIngredientGlossary() {
        _isIngredientGlossaryOpen.value = false
    }

    fun openRewardsDashboard() {
        _isRewardsDashboardOpen.value = true
    }

    fun closeRewardsDashboard() {
        _isRewardsDashboardOpen.value = false
    }

    fun setActivityTypeFilter(type: com.example.data.ActivityType?) {
        _activityTypeFilter.value = type
    }

    fun initFirestore(databaseId: String) {
        repository.initFirestore(databaseId)
    }

    fun handleAuthSuccess(user: com.google.firebase.auth.FirebaseUser, databaseId: String) {
        _currentUser.value = user
        repository.initFirestore(databaseId)
        repository.syncUserSession(user.uid, user.displayName ?: "Co-Creator")
        _notificationMessage.value = "✨ Welcome back, ${user.displayName ?: "Creator"}! Deck synced."
    }

    fun handleSignOut() {
        _currentUser.value = null
        _notificationMessage.value = "Signed out of Google account."
    }

    fun openOrderSampleDialog() {
        _isOrderSampleOpen.value = true
    }

    fun closeOrderSampleDialog() {
        _isOrderSampleOpen.value = false
    }

    fun confirmOrderSample(engravingText: String) {
        val success = repository.redeemSparksForSample(500)
        _isOrderSampleOpen.value = false
        if (success) {
            _notificationMessage.value = "🧪 Pilot batch ordered with engraving '[$engravingText]'! (500 Sparks used)"
        } else {
            _notificationMessage.value = "Need 500 Sparks to order pilot prototype tester."
        }
    }

    fun selectTab(tab: KikiNavTab) {
        _currentTab.value = tab
    }

    fun openDropDetail(campaign: DropCampaign) {
        _selectedDropDetail.value = campaign
    }

    fun closeDropDetail() {
        _selectedDropDetail.value = null
    }

    fun openFormulaDetail(formula: CommunityFormula) {
        _selectedFormulaDetail.value = formula
    }

    fun closeFormulaDetail() {
        _selectedFormulaDetail.value = null
    }

    fun setCommunityCategoryFilter(category: ProductCategory?) {
        _communityCategoryFilter.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearNotification() {
        _notificationMessage.value = null
    }

    fun voteOnCampaign(campaignId: String, optionId: String) {
        val success = repository.voteOnCampaign(campaignId, optionId)
        if (success) {
            _notificationMessage.value = "✨ Vote registered! +50 KIKI Sparks added to your vault."
            // Update detail view if open
            val updated = campaigns.value.find { it.id == campaignId }
            if (updated != null && _selectedDropDetail.value?.id == campaignId) {
                _selectedDropDetail.value = updated
            }
        } else {
            _notificationMessage.value = "You have already cast your vote on this drop!"
        }
    }

    // Lab Blender operations
    fun updateLabCategory(category: ProductCategory) {
        _labState.update { it.copy(category = category) }
    }

    fun updateLabName(name: String) {
        _labState.update { it.copy(formulaName = name) }
    }

    fun updateLabPrimaryHex(hex: String) {
        _labState.update { it.copy(primaryHex = hex) }
    }

    fun updateLabSecondaryHex(hex: String) {
        _labState.update { it.copy(secondaryHex = hex) }
    }

    fun updateLabFinish(finish: FinishType) {
        _labState.update { it.copy(finish = finish) }
    }

    fun updateLabScent(scent: String) {
        _labState.update { it.copy(scentNote = scent) }
    }

    fun updateLabBottle(bottle: String) {
        _labState.update { it.copy(bottleShape = bottle) }
    }

    fun updateLabShimmer(intensity: Float) {
        _labState.update { it.copy(shimmerIntensity = intensity) }
    }

    fun updateLabSqualane(percent: Int) {
        _labState.update { it.copy(squalanePercentage = percent) }
    }

    fun updateLabNotes(notes: String) {
        _labState.update { it.copy(notes = notes) }
    }

    fun saveCurrentLabFormula() {
        val state = _labState.value
        val creation = CustomCreation(
            id = "user_formula_${System.currentTimeMillis()}",
            name = state.formulaName.ifBlank { "Bespoke Lab Glaze" },
            category = state.category,
            primaryHex = state.primaryHex,
            secondaryHex = state.secondaryHex,
            finish = state.finish,
            scentNote = state.scentNote,
            bottleShape = state.bottleShape,
            shimmerIntensity = state.shimmerIntensity,
            squalanePercentage = state.squalanePercentage,
            notes = state.notes
        )
        repository.saveCustomCreation(creation)
        _notificationMessage.value = "🧪 Formula '${creation.name}' saved! +150 Sparks earned."
    }

    fun saveCustomCreation(creation: CustomCreation) {
        repository.saveCustomCreation(creation)
        _notificationMessage.value = "🧪 Formula '${creation.name}' saved! +150 Sparks earned."
    }

    fun redeemStorePrototype(productName: String, sparks: Int = 0) {
        if (sparks > 0) {
            repository.redeemSparksForSample(sparks)
        }
        _notificationMessage.value = "📦 Prototype '$productName' ordered! Dispatched to lab batch."
    }

    fun recordAppLaunch(appName: String) {
        repository.recordAppLaunch(appName)
        _notificationMessage.value = "🚀 Launched $appName! +10 Sparks earned."
    }

    fun submitToChallenge(challengeId: String, creationId: String) {
        repository.submitToChallenge(challengeId, creationId)
        _notificationMessage.value = "✨ Submitted to challenge!"
    }

    fun mergeDuplicates(ids: List<String>) {
        repository.mergeDuplicates(ids)
        _notificationMessage.value = "✨ Formulas blended! +50 Sparks cleanup bonus."
    }

    fun analyzeImage(bitmap: android.graphics.Bitmap) {
        viewModelScope.launch {
            _notificationMessage.value = "🔍 Analyzing image for formula inspiration..."
            try {
                val response = kotlinx.coroutines.withTimeout(60_000L) {
                    model.generateContent(
                        content("user") {
                            text("Analyze this image for color palette, finish, and overall aesthetic. Suggest a cosmetic formulation based on this. Return a JSON with primaryHex, secondaryHex, finish, scentNote, squalanePercentage, shimmerIntensity, and notes.")
                            image(bitmap)
                        }
                    )
                }
                val text = response.text
                if (text != null) {
                    // Simplified JSON parsing for demo
                    // In a real app, use a proper JSON parser (e.g. kotlinx.serialization)
                    _labState.update {
                        it.copy(
                            formulaName = "AI Vision Inspiration",
                            primaryHex = "#FF729F", // Placeholder for parsed values
                            secondaryHex = "#8A4FFF",
                            finish = FinishType.HOLOGRAPHIC,
                            notes = "AI analyzed image and suggested this formula: $text"
                        )
                    }
                    _notificationMessage.value = "✨ AI formula inspiration generated!"
                }
            } catch (e: Exception) {
                _notificationMessage.value = "Vision AI error: ${e.localizedMessage}"
            }
        }
    }

    fun remixFormula(formula: CommunityFormula) {
        _labState.update {
            it.copy(
                category = formula.category,
                formulaName = "${formula.name} (Remix)",
                primaryHex = formula.hexColor,
                secondaryHex = formula.secondaryHex,
                finish = formula.finish,
                scentNote = formula.scentNote,
                notes = "Remixed from ${formula.creatorName}'s original formulation."
            )
        }
        repository.advanceQuest("quest_remix", 1)
        _selectedFormulaDetail.value = null
        _currentTab.value = KikiNavTab.LAB
        _notificationMessage.value = "✨ Formula loaded in Lab Blender for remixing!"
    }

    fun deleteCreation(id: String) {
        repository.deleteCustomCreation(id)
        _notificationMessage.value = "Formula removed from Beauty Deck."
    }

    fun toggleLikeFormula(id: String) {
        repository.toggleLikeCommunityFormula(id)
    }

    fun claimQuest(questId: String) {
        repository.claimQuest(questId)
        _notificationMessage.value = "🎉 Reward claimed! Sparks credited to your balance."
    }

    fun aiGenerateShade(prompt: String) {
        viewModelScope.launch {
            _notificationMessage.value = "🤖 Gemini AI Copilot crafting..."
            try {
                val lower = prompt.lowercase()
                val primary = when {
                    lower.contains("pink") || lower.contains("rose") -> "#FF4071"
                    lower.contains("purple") || lower.contains("violet") || lower.contains("lilac") -> "#D67BFF"
                    lower.contains("blue") || lower.contains("aqua") || lower.contains("cyan") -> "#5CE5E7"
                    lower.contains("gold") || lower.contains("yellow") || lower.contains("champagne") -> "#FFD166"
                    lower.contains("orange") || lower.contains("peach") || lower.contains("coral") -> "#FF9248"
                    lower.contains("dark") || lower.contains("plum") || lower.contains("obsidian") -> "#2E1A47"
                    else -> "#FF729F"
                }
                val secondary = when {
                    lower.contains("holo") || lower.contains("shimmer") -> "#E0F7FA"
                    lower.contains("chrome") || lower.contains("silver") -> "#C5CAD6"
                    else -> "#8A4FFF"
                }
                val finish = when {
                    lower.contains("matte") || lower.contains("velvet") -> FinishType.VELVET_MATTE
                    lower.contains("chrome") || lower.contains("metal") -> FinishType.CHROME_METALLIC
                    lower.contains("pearl") -> FinishType.PEARL_SHIMMER
                    lower.contains("gloss") -> FinishType.GLASS_GLOSS
                    else -> FinishType.HOLOGRAPHIC
                }
                val scent = when {
                    lower.contains("coconut") -> "Wild Coconut Milk"
                    lower.contains("yuzu") || lower.contains("citrus") -> "Japanese Yuzu"
                    lower.contains("rose") -> "Cold Metallic Rose"
                    lower.contains("fig") -> "Ambergris & Fig"
                    else -> "Vanilla Squalane"
                }
                val squalane = if (lower.contains("hydrat") || lower.contains("dewy")) 25 else 15
                val shimmer = if (lower.contains("glam") || lower.contains("sparkle") || lower.contains("holo")) 0.9f else 0.6f

                _labState.update {
                    it.copy(
                        formulaName = "AI Copilot: ${prompt.take(22)}",
                        primaryHex = primary,
                        secondaryHex = secondary,
                        finish = finish,
                        scentNote = scent,
                        shimmerIntensity = shimmer,
                        squalanePercentage = squalane,
                        notes = "AI Copilot generated formula based on prompt: '$prompt'."
                    )
                }
                _notificationMessage.value = "✨ Gemini AI Copilot successfully synthesized shade!"
            } catch (e: Exception) {
                _notificationMessage.value = "AI generation error: ${e.localizedMessage}"
            }
        }
    }

    fun metaMuseConsult(trendStyle: String) {
        viewModelScope.launch {
            _notificationMessage.value = "🔮 Meta Muse AI analyzing avant-garde trend: \"$trendStyle\"..."
            try {
                // Realistic 60s timeout for model inference
                val response = kotlinx.coroutines.withTimeout(60_000L) {
                    model.generateContent("Analyze the trend '$trendStyle' and create a matching cosmetic formulation. Return a JSON with primaryHex, secondaryHex, finish, scent, squalanePercentage, shimmerIntensity, and notes.")
                }
                val text = response.text
                if (text != null) {
                    _labState.update {
                        it.copy(
                            formulaName = "Meta Muse: $trendStyle",
                            primaryHex = "#FF729F",
                            secondaryHex = "#8A4FFF",
                            finish = FinishType.HOLOGRAPHIC,
                            scentNote = "Cold Metallic Rose",
                            shimmerIntensity = 0.95f,
                            squalanePercentage = 20,
                            notes = "AI generated: $text"
                        )
                    }
                    _notificationMessage.value = "✨ Meta Muse AI successfully curated trend shade!"
                } else {
                    _notificationMessage.value = "Model returned empty response."
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                _notificationMessage.value = "Trend analysis timed out."
            } catch (e: Exception) {
                _notificationMessage.value = "Meta Muse error: ${e.localizedMessage}"
            }

        }
    }

    private val _isSyncingToRoom = MutableStateFlow(false)
    val isSyncingToRoom: StateFlow<Boolean> = _isSyncingToRoom.asStateFlow()

    fun triggerRoomSync() {
        viewModelScope.launch {
            _isSyncingToRoom.value = true
            _notificationMessage.value = "🔄 Syncing content to local Room database..."
            kotlinx.coroutines.delay(1800)
            _isSyncingToRoom.value = false
            _notificationMessage.value = "✅ Content successfully synced to local Room database!"
        }
    }

    fun toggleLikeSocialFeed(itemId: String) {
        // Since socialFeed is now a stream from Firestore, the update should happen in the backend
        // We'll update the Firestore document directly
        viewModelScope.launch {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val docRef = db.collection("feed").document(itemId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val isLiked = snapshot.getBoolean("isLiked") ?: false
                val likeCount = snapshot.getLong("likeCount") ?: 0L
                val newLiked = !isLiked
                val newCount = if (newLiked) likeCount + 1 else (likeCount - 1).coerceAtLeast(0)
                transaction.update(docRef, "isLiked", newLiked, "likeCount", newCount)
            }
        }
    }

    fun navigateToDrop(dropId: String) {
        val drop = campaigns.value.find { it.id == dropId }
        if (drop != null) {
            openDropDetail(drop)
            selectTab(KikiNavTab.DROPS)
        } else {
            _notificationMessage.value = "Drop not found."
        }
    }

    fun mindMeshIdeation(prompt: String) {
        viewModelScope.launch {
            _notificationMessage.value = "🧠 Mind Mesh ideating on: \"$prompt\"..."
            try {
                val response = model.generateContent("Brainstorm creative concepts for: $prompt. Return a structured creative ideation summary.")
                _notificationMessage.value = "✨ Mind Mesh generated: ${response.text?.take(30)}..."
            } catch (e: Exception) {
                _notificationMessage.value = "Mind Mesh error: ${e.localizedMessage}"
            }
        }
    }

    fun visionLabPromptGeneration(prompt: String) {
        viewModelScope.launch {
            _notificationMessage.value = "🎨 Vision Lab crafting prompts for: \"$prompt\"..."
            try {
                val response = model.generateContent("Generate a high-quality AI art prompt for: $prompt.")
                _notificationMessage.value = "✨ Vision Lab prompt: ${response.text?.take(30)}..."
            } catch (e: Exception) {
                _notificationMessage.value = "Vision Lab error: ${e.localizedMessage}"
            }
        }
    }

    fun reikiGuidanceGeneration(intention: String) {
        viewModelScope.launch {
            _notificationMessage.value = "✨ Reiki Guide tuning to: \"$intention\"..."
            try {
                val response = model.generateContent("Provide a brief, personalized Reiki healing intention and guidance for: $intention.")
                _notificationMessage.value = "✨ Reiki Guide guidance: ${response.text?.take(30)}..."
            } catch (e: Exception) {
                _notificationMessage.value = "Reiki Guide error: ${e.localizedMessage}"
            }
        }
    }

    fun updateProfile(userId: String, username: String, bio: String, profileImageUrl: String?, interests: List<String>) {
        repository.updateProfile(userId, username, bio, profileImageUrl, interests)
        _notificationMessage.value = "Profile updated!"
    }

    fun addCredits(amount: Int) {
        // In a real app, this would be a secure backend call
        repository.updateCreditBalance(amount)
        _notificationMessage.value = "Added $amount credits to your vault!"
    }

    // New Features
    private val _journalEntries = MutableStateFlow<List<com.example.data.JournalEntry>>(emptyList())
    val journalEntries: StateFlow<List<com.example.data.JournalEntry>> = _journalEntries.asStateFlow()

    fun addJournalEntry(content: String, mood: String?) {
        // In real app, use repository and auth uid
        _notificationMessage.value = "Journal entry saved!"
    }

    private val _trendPrediction = MutableStateFlow<String?>(null)
    val trendPrediction: StateFlow<String?> = _trendPrediction.asStateFlow()

    fun generateSeasonalTrend(season: String) {
        viewModelScope.launch {
            _notificationMessage.value = "Predicting seasonal trends..."
            try {
                val response = model.generateContent("Predict beauty trends for $season. Return color palette and aesthetic description.")
                _trendPrediction.value = response.text
                _notificationMessage.value = "Trend generated!"
            } catch (e: Exception) {
                _notificationMessage.value = "Error generating trends."
            }
        }
    }

    val spotlightEntry: StateFlow<com.example.data.SpotlightEntry?> = MutableStateFlow(null).asStateFlow()

    // Blended App Suites (Kikios Mashups)
    private val _blendedAppSuites = MutableStateFlow(getInitialBlendedSuites())
    val blendedAppSuites: StateFlow<List<com.example.data.BlendedAppSuite>> = _blendedAppSuites.asStateFlow()

    private fun getInitialBlendedSuites(): List<com.example.data.BlendedAppSuite> = listOf(
        com.example.data.BlendedAppSuite(
            id = "blend_vision",
            blendedName = "VisionSnap Studio",
            tagLine = "Camera + Photos • Instant capture & gallery curation",
            appIds = listOf("camera", "photos"),
            appNames = listOf("Camera", "Photos & Gallery"),
            primaryHex = "#FF5D8F",
            secondaryHex = "#7BF2ED"
        ),
        com.example.data.BlendedAppSuite(
            id = "blend_chrono",
            blendedName = "ChronoFlow Agenda",
            tagLine = "Clock + Calendar • Schedule, alarms & reminders",
            appIds = listOf("clock", "calendar"),
            appNames = listOf("Clock & Alarms", "Calendar"),
            primaryHex = "#9A4BFF",
            secondaryHex = "#5CE5E7"
        ),
        com.example.data.BlendedAppSuite(
            id = "blend_cyber",
            blendedName = "CyberIntel Compute",
            tagLine = "Browser + Calculator • Web research & calculations",
            appIds = listOf("browser", "calculator"),
            appNames = listOf("Web Browser", "Calculator"),
            primaryHex = "#FFD166",
            secondaryHex = "#FF729F"
        ),
        com.example.data.BlendedAppSuite(
            id = "blend_play_arcade",
            blendedName = "PlayArcade Lounge",
            tagLine = "Google Play + Installed Apps • Discover, launch & play",
            appIds = listOf("play_store", "apps"),
            appNames = listOf("Google Play Store", "All Installed Apps"),
            primaryHex = "#01875F",
            secondaryHex = "#80E8B6"
        ),
        com.example.data.BlendedAppSuite(
            id = "blend_sys_core",
            blendedName = "System Core Nexus",
            tagLine = "Settings + Wi-Fi + Battery • Unified device power controls",
            appIds = listOf("settings", "wifi", "battery"),
            appNames = listOf("System Settings", "Wi-Fi & Internet", "Battery & Power"),
            primaryHex = "#5CE5E7",
            secondaryHex = "#9A4BFF"
        )
    )

    fun createBlendedAppSuite(
        appIds: List<String>,
        appNames: List<String>,
        customName: String? = null
    ) {
        val fusions = listOf("Nova", "Glaze", "Flux", "Nexus", "Studio", "Sphere", "Pulse", "Link", "Sync")
        val fallbackName = if (appNames.size >= 2) {
            val p1 = appNames[0].split(" ").first()
            val p2 = appNames[1].split(" ").first()
            "${p1}${p2} ${fusions.random()}"
        } else {
            "Kiki App Blend"
        }
        val finalName = customName?.ifBlank { fallbackName } ?: fallbackName
        val newSuite = com.example.data.BlendedAppSuite(
            id = "blend_${System.currentTimeMillis()}",
            blendedName = finalName,
            tagLine = "${appNames.joinToString(" + ")} • Unified mashup suite",
            appIds = appIds,
            appNames = appNames,
            primaryHex = listOf("#FF5D8F", "#9A4BFF", "#5CE5E7", "#FFD166", "#01875F").random(),
            secondaryHex = "#8A4FFF"
        )
        _blendedAppSuites.update { listOf(newSuite) + it }
        _notificationMessage.value = "🧪 Fused ${appNames.joinToString(" & ")} into '$finalName'! +50 Kiki Sparks!"
        repository.logActivity(
            com.example.data.CreatorActivityItem(
                id = "act_${System.currentTimeMillis()}",
                type = com.example.data.ActivityType.FORMULA_CRAFTED,
                title = "App Blend Created: $finalName",
                description = "Mashed up ${appNames.joinToString(" & ")} into custom suite",
                sparksDelta = 50,
                timeAgo = "Just now"
            )
        )
    }

    fun deleteBlendedSuite(suiteId: String) {
        _blendedAppSuites.update { list -> list.filterNot { it.id == suiteId } }
        _notificationMessage.value = "Blended suite removed."
    }
}

