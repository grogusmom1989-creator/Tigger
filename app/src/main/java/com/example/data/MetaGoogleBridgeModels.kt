package com.example.data

enum class BridgeStatus(val label: String, val badgeColor: String) {
    ACTIVE("Connected & Live", "#00F5D4"),
    SYNCING("Syncing Buffers...", "#FEE440"),
    STANDBY("Standby / Idle", "#B388FF"),
    PAUSED("Paused", "#9E9E9E")
}

enum class BridgeProtocol(val label: String, val icon: String) {
    ZIP_ARCHIVE("Drive ZIP Pipeline", "📦"),
    GEMINI_REASONING("Gemini Co-Reasoning", "✨"),
    SOCIAL_TREND_STREAM("Meta Social Signals", "🌐"),
    COMMERCE_WEBHOOK("Storefront Checkout Bridge", "🛍️")
}

data class BridgeConnector(
    val id: String,
    val name: String,
    val sourcePlatform: String,
    val destinationPlatform: String,
    val protocol: BridgeProtocol,
    val status: BridgeStatus,
    val latencyMs: Int,
    val itemsTransferred: Int,
    val lastSyncTime: String,
    val description: String,
    val isEnabled: Boolean = true,
    val customEndpoint: String? = null
)

data class MuseStoreProduct(
    val id: String,
    val title: String,
    val category: String,
    val priceUsd: Double,
    val sparksPrice: Int,
    val hexColor: String,
    val secondaryHex: String,
    val finish: String,
    val description: String,
    val stockUnits: Int,
    val tags: List<String>,
    val museBridgeSynced: Boolean = true,
    val isSampleKit: Boolean = false
)

data class BlendRoom(
    val roomId: String,
    val roomCode: String,
    val roomName: String,
    val hostCreator: String,
    val activeParticipantCount: Int,
    val activeShadeHex: String,
    val finishName: String,
    val squalanePercent: Int,
    val shimmerIntensity: Float,
    val isLive: Boolean = true,
    val lastAction: String = "Formula adjusted by host"
)

enum class AvatarEvolutionStage(
    val stageLevel: Int,
    val title: String,
    val badgeIcon: String,
    val requiredFormulas: Int,
    val requiredQuests: Int,
    val auraColorHex: String,
    val auraDescription: String
) {
    NOVICE_ALCHEMIST(1, "Novice Alchemist", "🧪", 0, 0, "#B388FF", "Soft Lavender Spark"),
    CHROMATIC_WEAVER(2, "Chromatic Weaver", "🎨", 3, 2, "#00F5D4", "Cyan Hologram Aura"),
    CYBER_NEONIST(3, "Cyber Neonist", "⚡", 6, 5, "#FF007F", "Magenta Pulse Halo"),
    ETHEREAL_LUMINARY(4, "Ethereal Luminary", "✨", 12, 10, "#FEE440", "Solar Gold Prismatic Corona"),
    ASTRAL_ARCHON(5, "Astral Archon", "🌌", 20, 15, "#7B2CBF", "Cosmic Stardust Nebula")
}

data class DynamicAvatarState(
    val stage: AvatarEvolutionStage,
    val nextStage: AvatarEvolutionStage?,
    val progressPercent: Float,
    val unlockedAuras: List<String>,
    val currentAccessory: String,
    val evolutionRankTitle: String
)

data class MuseExtractionManifest(
    val id: String,
    val title: String,
    val totalFormulas: Int,
    val totalTexturePacks: Int,
    val totalStems: Int,
    val totalSpecs: Int,
    val estimatedArchiveSizeMb: Double,
    val lastSyncTimestamp: String
)

data class MuseExtractedFormula(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val primaryHex: String,
    val secondaryHex: String,
    val finish: FinishType,
    val squalanePercentage: Int,
    val shimmerIntensity: Float,
    val scentNote: String,
    val bottleShape: String,
    val notes: String
)

