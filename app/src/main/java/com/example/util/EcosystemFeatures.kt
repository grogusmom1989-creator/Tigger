package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.viewmodel.KikiNavTab

data class EcosystemFeatureItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val targetTab: KikiNavTab? = null,
    val actionLabel: String = "Open",
    val accentHex: String = "#FF729F",
    val systemAction: ((Context) -> Unit)? = null
)

object EcosystemFeatures {

    val CATEGORIES = listOf(
        "All (35+)",
        "Command Hub",
        "Apps & Blender",
        "Lab & AI",
        "Community & Social",
        "Vault & Store",
        "Ambient Utilities"
    )

    fun getAllFeatures(): List<EcosystemFeatureItem> = listOf(
        // --- 1. COMMAND HUB & TELEMETRY ---
        EcosystemFeatureItem(
            id = "cmd_telemetry",
            title = "Hardware Telemetry Radar",
            category = "Command Hub",
            description = "Real-time battery gauge, RAM, disk storage & network speed monitor",
            icon = Icons.Default.BatteryChargingFull,
            targetTab = KikiNavTab.DASHBOARD,
            actionLabel = "Monitor",
            accentHex = "#80E8B6"
        ),
        EcosystemFeatureItem(
            id = "cmd_sparks",
            title = "Sparks Economy Engine",
            category = "Command Hub",
            description = "Gamified token rewards, streak bonus multiplier & daily check-in",
            icon = Icons.Default.AutoAwesome,
            targetTab = KikiNavTab.DASHBOARD,
            actionLabel = "View Sparks",
            accentHex = "#FF729F"
        ),
        EcosystemFeatureItem(
            id = "cmd_drops_radar",
            title = "Active Drop Radar",
            category = "Command Hub",
            description = "Live voting countdown & batch production tracker",
            icon = Icons.Default.Schedule,
            targetTab = KikiNavTab.DROPS,
            actionLabel = "Vote Now",
            accentHex = "#5CE5E7"
        ),
        EcosystemFeatureItem(
            id = "cmd_suite_launcher",
            title = "Blended Suite Quick-Launch",
            category = "Command Hub",
            description = "Fast one-tap launch bar for all your created app suites",
            icon = Icons.Default.Science,
            targetTab = KikiNavTab.LAUNCHER,
            actionLabel = "Launch",
            accentHex = "#8A4FFF"
        ),
        EcosystemFeatureItem(
            id = "cmd_quests",
            title = "Daily Quests & Mindful Moments",
            category = "Command Hub",
            description = "Complete micro-tasks and earn sparks towards creator tiers",
            icon = Icons.Default.Check,
            targetTab = KikiNavTab.VAULT,
            actionLabel = "Quests",
            accentHex = "#FFD166"
        ),

        // --- 2. APPS & BLENDER STATION ---
        EcosystemFeatureItem(
            id = "apps_blender",
            title = "Phone & Tablet App Blender",
            category = "Apps & Blender",
            description = "Fuse system tools & apps into custom dual suites (+50 Sparks)",
            icon = Icons.Default.Science,
            targetTab = KikiNavTab.LAUNCHER,
            actionLabel = "Blend",
            accentHex = "#FF729F"
        ),
        EcosystemFeatureItem(
            id = "apps_play_store",
            title = "Google Play Store Gateway",
            category = "Apps & Blender",
            description = "Direct shortcut to discover, install & update phone and tablet apps",
            icon = Icons.Default.Shop,
            targetTab = null,
            systemAction = { context -> DeviceSystemManager.openGooglePlay(context) },
            actionLabel = "Launch Store",
            accentHex = "#01875F"
        ),
        EcosystemFeatureItem(
            id = "apps_system_tools",
            title = "Device System Tools Deck",
            category = "Apps & Blender",
            description = "Camera, Photos, Calendar, Settings, Clock, Calculator & Files",
            icon = Icons.Default.Build,
            targetTab = KikiNavTab.LAUNCHER,
            actionLabel = "Tools",
            accentHex = "#8A4FFF"
        ),
        EcosystemFeatureItem(
            id = "apps_installed_apps",
            title = "All Installed Apps Explorer",
            category = "Apps & Blender",
            description = "Search and launch any application installed on your Android device",
            icon = Icons.Default.Apps,
            targetTab = KikiNavTab.LAUNCHER,
            actionLabel = "Apps",
            accentHex = "#5CE5E7"
        ),
        EcosystemFeatureItem(
            id = "apps_pwa_portal",
            title = "Muse PWA & Web Engine",
            category = "Apps & Blender",
            description = "Full-screen embedded web apps, Muse networks & custom portals",
            icon = Icons.Default.Language,
            targetTab = KikiNavTab.WEB_PORTAL,
            actionLabel = "Browse",
            accentHex = "#80E8B6"
        ),
        EcosystemFeatureItem(
            id = "apps_kikiverse",
            title = "KikiVerse & Shrine Audio",
            category = "Apps & Blender",
            description = "Interactive lore portals, photo gallery & streaming music player",
            icon = Icons.Default.MusicNote,
            targetTab = KikiNavTab.KIKIVERSE,
            actionLabel = "Enter",
            accentHex = "#FF729F"
        ),

        // --- 3. LAB & AI HUB ---
        EcosystemFeatureItem(
            id = "lab_mixer",
            title = "Formula Color Studio",
            category = "Lab & AI",
            description = "Synthesize custom beauty shades with dual-hex chromatic gradients",
            icon = Icons.Default.Science,
            targetTab = KikiNavTab.LAB,
            actionLabel = "Mix Shade",
            accentHex = "#FF729F"
        ),
        EcosystemFeatureItem(
            id = "lab_finishes",
            title = "Texture & Finish Engine",
            category = "Lab & AI",
            description = "Holo Glaze, Liquid Chrome, Prismatic Pearl, Glass Gloss & Cloud Matte",
            icon = Icons.Default.AutoAwesome,
            targetTab = KikiNavTab.LAB,
            actionLabel = "Set Finish",
            accentHex = "#5CE5E7"
        ),
        EcosystemFeatureItem(
            id = "lab_ingredients",
            title = "Squalane & Scent Formulator",
            category = "Lab & AI",
            description = "Fine-tune squalane hydration percentage & botanical aroma notes",
            icon = Icons.Default.Science,
            targetTab = KikiNavTab.LAB,
            actionLabel = "Formulate",
            accentHex = "#80E8B6"
        ),
        EcosystemFeatureItem(
            id = "lab_ai_companion",
            title = "AI Creative Companion",
            category = "Lab & AI",
            description = "Gemini-powered creative copilot for formulation and concept design",
            icon = Icons.Default.AutoAwesome,
            targetTab = KikiNavTab.AI_COMPANION,
            actionLabel = "Chat AI",
            accentHex = "#8A4FFF"
        ),
        EcosystemFeatureItem(
            id = "lab_trend_predictor",
            title = "AI Trend Predictor",
            category = "Lab & AI",
            description = "Predictive intelligence for upcoming colorways & seasonal aesthetics",
            icon = Icons.Default.Stars,
            targetTab = KikiNavTab.TREND_PREDICTOR,
            actionLabel = "Forecast",
            accentHex = "#FFD166"
        ),
        EcosystemFeatureItem(
            id = "lab_glossary",
            title = "Clean Ingredient Directory",
            category = "Lab & AI",
            description = "EWG safety ratings, skin barrier benefits & clean beauty index",
            icon = Icons.Default.Info,
            targetTab = KikiNavTab.LAB,
            actionLabel = "Glossary",
            accentHex = "#80E8B6"
        ),
        EcosystemFeatureItem(
            id = "lab_prototype",
            title = "Physical Sample Prototype",
            category = "Lab & AI",
            description = "Order real physical formulation samples straight from the lab",
            icon = Icons.Default.Shop,
            targetTab = KikiNavTab.LAB,
            actionLabel = "Order Sample",
            accentHex = "#FF729F"
        ),

        // --- 4. COMMUNITY & SOCIAL ---
        EcosystemFeatureItem(
            id = "comm_drops",
            title = "Co-Creation Product Drops",
            category = "Community & Social",
            description = "Vote on shades, finishes, and packaging for real production batches",
            icon = Icons.Default.Schedule,
            targetTab = KikiNavTab.DROPS,
            actionLabel = "Vote",
            accentHex = "#5CE5E7"
        ),
        EcosystemFeatureItem(
            id = "comm_formulas",
            title = "Community Formula Gallery",
            category = "Community & Social",
            description = "Browse trending creations, like, bookmark, and share worldwide",
            icon = Icons.Default.Science,
            targetTab = KikiNavTab.COMMUNITY,
            actionLabel = "Browse",
            accentHex = "#FF729F"
        ),
        EcosystemFeatureItem(
            id = "comm_remix",
            title = "Formula Remix Engine",
            category = "Community & Social",
            description = "Clone community creations and tweak them in your private lab",
            icon = Icons.Default.AutoAwesome,
            targetTab = KikiNavTab.COMMUNITY,
            actionLabel = "Remix",
            accentHex = "#8A4FFF"
        ),
        EcosystemFeatureItem(
            id = "comm_social_feed",
            title = "KikiLink Social Feed",
            category = "Community & Social",
            description = "Post beauty formulas, share imagery & engage with creators",
            icon = Icons.Default.Language,
            targetTab = KikiNavTab.SOCIAL,
            actionLabel = "Feed",
            accentHex = "#FFD166"
        ),
        EcosystemFeatureItem(
            id = "comm_chat",
            title = "Direct Messaging & Chat",
            category = "Community & Social",
            description = "Real-time peer chat, friend invites & collaboration",
            icon = Icons.Default.Message,
            targetTab = KikiNavTab.SOCIAL,
            actionLabel = "Chat",
            accentHex = "#80E8B6"
        ),
        EcosystemFeatureItem(
            id = "comm_challenges",
            title = "Weekly Co-Creation Challenges",
            category = "Community & Social",
            description = "Submit recipes to theme challenges and win big Spark pools",
            icon = Icons.Default.Stars,
            targetTab = KikiNavTab.COMMUNITY,
            actionLabel = "Challenges",
            accentHex = "#FF729F"
        ),
        EcosystemFeatureItem(
            id = "comm_leaderboard",
            title = "Creator Leaderboard",
            category = "Community & Social",
            description = "Global creator rankings by lifetime sparks and unlocked badges",
            icon = Icons.Default.Stars,
            targetTab = KikiNavTab.COMMUNITY,
            actionLabel = "Rankings",
            accentHex = "#FFD166"
        ),

        // --- 5. VAULT & COMMERCE ---
        EcosystemFeatureItem(
            id = "vault_charms",
            title = "Digital Charms & Badges",
            category = "Vault & Store",
            description = "Verifiable digital charms (Common to Legendary) in your vault",
            icon = Icons.Default.Stars,
            targetTab = KikiNavTab.VAULT,
            actionLabel = "Charms",
            accentHex = "#FF729F"
        ),
        EcosystemFeatureItem(
            id = "vault_tiers",
            title = "Creator Tier Milestones",
            category = "Vault & Store",
            description = "Unlock higher lab voting weights, prototype perks & VIP access",
            icon = Icons.Default.Build,
            targetTab = KikiNavTab.VAULT,
            actionLabel = "Tiers",
            accentHex = "#8A4FFF"
        ),
        EcosystemFeatureItem(
            id = "vault_journal",
            title = "Daily Beauty & Creative Journal",
            category = "Vault & Store",
            description = "Personal mood diary, aesthetic logs & timestamped reflections",
            icon = Icons.Default.Book,
            targetTab = KikiNavTab.JOURNAL,
            actionLabel = "Journal",
            accentHex = "#5CE5E7"
        ),
        EcosystemFeatureItem(
            id = "vault_store",
            title = "Shopify Storefront",
            category = "Vault & Store",
            description = "Browse catalog, purchase physical products & redeem credits",
            icon = Icons.Default.Shop,
            targetTab = KikiNavTab.STORE,
            actionLabel = "Shop",
            accentHex = "#80E8B6"
        ),
        EcosystemFeatureItem(
            id = "vault_drive",
            title = "KikiDrive & Cloud Backup",
            category = "Vault & Store",
            description = "Firestore sync across phone & tablet with Google Sign-In",
            icon = Icons.Default.Folder,
            targetTab = KikiNavTab.DRIVE,
            actionLabel = "Drive",
            accentHex = "#8A4FFF"
        ),
        EcosystemFeatureItem(
            id = "vault_security",
            title = "Security & Session Dashboard",
            category = "Vault & Store",
            description = "Credential Manager authentication, encryption & privacy controls",
            icon = Icons.Default.Settings,
            targetTab = KikiNavTab.SYNC,
            actionLabel = "Security",
            accentHex = "#80E8B6"
        ),
        EcosystemFeatureItem(
            id = "vault_profile",
            title = "Alchemist Profile Deck",
            category = "Vault & Store",
            description = "Customize avatar, handle, biography, interests & wallet address",
            icon = Icons.Default.Person,
            targetTab = KikiNavTab.PROFILE,
            actionLabel = "Profile",
            accentHex = "#FF729F"
        ),

        // --- 6. AMBIENT UTILITIES ---
        EcosystemFeatureItem(
            id = "util_weather",
            title = "Weather Radar & Microclimate",
            category = "Ambient Utilities",
            description = "Ambient temperature, humidity & conditions telemetry",
            icon = Icons.Default.Wifi,
            targetTab = KikiNavTab.WEATHER,
            actionLabel = "Weather",
            accentHex = "#5CE5E7"
        ),
        EcosystemFeatureItem(
            id = "util_news",
            title = "Curated News & Tech Stream",
            category = "Ambient Utilities",
            description = "Live updates and trends from the digital beauty & AI world",
            icon = Icons.Default.Language,
            targetTab = KikiNavTab.NEWS,
            actionLabel = "News",
            accentHex = "#FFD166"
        ),
        EcosystemFeatureItem(
            id = "util_reiki",
            title = "Reiki Guide & Mindful Sounds",
            category = "Ambient Utilities",
            description = "Ambient mindfulness soundscape & grounding energy module",
            icon = Icons.Default.PlayArrow,
            targetTab = KikiNavTab.PORTAL,
            actionLabel = "Listen",
            accentHex = "#80E8B6"
        ),
        EcosystemFeatureItem(
            id = "util_play_updates",
            title = "Play Core Live Updates",
            category = "Ambient Utilities",
            description = "Automatic Google Play in-app version update detection",
            icon = Icons.Default.Shop,
            targetTab = null,
            systemAction = { context -> DeviceSystemManager.openGooglePlay(context) },
            actionLabel = "Check Updates",
            accentHex = "#01875F"
        )
    )
}
