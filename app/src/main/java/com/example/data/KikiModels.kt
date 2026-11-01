package com.example.data

enum class ProductCategory(val displayName: String, val icon: String) {
    LIP_OIL("Lip Oil & Gloss", "💄"),
    NAIL_LACQUER("Peel Lacquer", "💅"),
    HIGHLIGHTER("Glow Stick", "✨"),
    SCENT_MIST("Velvet Mist", "🌸"),
    CHEEK_TINT("Dewy Tint", "🪄")
}

enum class FinishType(val displayName: String, val shimmerAlpha: Float) {
    HOLOGRAPHIC("Holo Glaze", 0.9f),
    CHROME_METALLIC("Liquid Chrome", 0.75f),
    PEARL_SHIMMER("Prismatic Pearl", 0.5f),
    GLASS_GLOSS("Mirror Gloss", 0.2f),
    VELVET_MATTE("Cloud Matte", 0.0f)
}

data class VoteOption(
    val id: String,
    val label: String,
    val description: String,
    val voteCount: Int,
    val hexColor: String,
    val finish: FinishType
)

enum class DropStage(val label: String) {
    VOTING_SHADE("Shade Voting"),
    VOTING_FINISH("Finish Voting"),
    VOTING_PACKAGING("Packaging Select"),
    IN_PRODUCTION("In Lab Production")
}

data class DropCampaign(
    val id: String,
    val dropNumber: String,
    val title: String,
    val subtitle: String,
    val category: ProductCategory,
    val hoursRemaining: Int,
    val stage: DropStage,
    val totalVotes: Int,
    val userVotedOptionId: String? = null,
    val options: List<VoteOption>,
    val fullDescription: String,
    val productionDate: String = "Summer 2026 Batch"
)

data class CommunityFormula(
    val id: String,
    val name: String,
    val creatorName: String,
    val creatorHandle: String,
    val category: ProductCategory,
    val hexColor: String,
    val secondaryHex: String,
    val finish: FinishType,
    val scentNote: String,
    val upvotes: Int,
    val isLiked: Boolean = false,
    val tags: List<String>
)

data class CustomCreation(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val primaryHex: String,
    val secondaryHex: String,
    val finish: FinishType,
    val scentNote: String,
    val bottleShape: String,
    val shimmerIntensity: Float,
    val squalanePercentage: Int,
    val notes: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val rewardSparks: Int,
    val targetCount: Int,
    val currentCount: Int,
    val claimed: Boolean,
    val isMindfulMoment: Boolean = false
) {
    val isCompleted: Boolean get() = currentCount >= targetCount
}

data class DigitalCharm(
    val id: String,
    val name: String,
    val rarity: String, // Common, Rare, Ultra-Rare, Legendary
    val badgeNumber: String,
    val dateUnlocked: String,
    val description: String,
    val accentHex: String
)

data class CreatorProfile(
    val username: String = "SirenAlchemist",
    val handle: String = "@siren_kiki",
    val bio: String = "Digital alchemist & sparkle seeker.",
    val profileImageUrl: String? = null,
    val sparksBalance: Int = 1850,
    val lifetimeSparksEarned: Int = 3450,
    val tierName: String = "Lab Chemist",
    val tierLevel: Int = 3,
    val streakDays: Int = 14,
    val totalFormulasCreated: Int = 6,
    val totalVotesCast: Int = 29,
    val creditBalance: Int = 0,
    val interests: List<String> = emptyList()
)

enum class ActivityType(val label: String, val icon: String) {
    VOTE_CAST("Vote Cast", "🗳️"),
    FORMULA_CRAFTED("Formula Blended", "🧪"),
    FORMULA_REMIXED("Formula Remixed", "✨"),
    QUEST_CLAIMED("Quest Reward", "⚡"),
    PROTOTYPE_ORDERED("Prototype Order", "📦"),
    STREAK_BONUS("Streak Bonus", "🔥"),
    APP_LAUNCHED("App Launched", "🚀")
}

data class CreatorActivityItem(
    val id: String,
    val type: ActivityType,
    val title: String,
    val description: String,
    val sparksDelta: Int,
    val timeAgo: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class TierMilestone(
    val level: Int,
    val name: String,
    val requiredSparks: Int,
    val perks: List<String>,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class IngredientInfo(
    val name: String,
    val category: String,
    val benefit: String,
    val safetyScore: String,
    val description: String
)


data class ShopifyProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val imageUrl: String
)

data class SocialFeedItem(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val content: String,
    val imageUrl: String? = null,
    val dropId: String? = null,
    val timestamp: String = "Just now",
    val isLiked: Boolean = false,
    val likeCount: Int = 0
)

enum class MediaType {
    IMAGE, VIDEO
}

data class MediaItem(
    val id: String,
    val title: String,
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val duration: String? = null
)

data class LeaderboardItem(
    val userId: String = "",
    val username: String = "",
    val sparksBalance: Long = 0L
)

data class BlogEntry(val id: String, val title: String, val content: String, val date: String)
data class MusicTrack(val id: String, val title: String, val artist: String, val url: String)
data class ShrinePage(val id: String, val title: String, val description: String, val imageUrl: String)
data class GalleryItem(val id: String, val title: String, val imageUrl: String)

data class UserActivity(
    val id: String = "",
    val userId: String = "",
    val activityType: String = "",
    val pointsEarned: Int = 0,
    val timestamp: com.google.firebase.Timestamp? = null
)

data class Reward(
    val id: String = "",
    val name: String = "",
    val cost: Int = 0,
    val description: String = "",
    val imageUrl: String = ""
)

data class UserReward(
    val id: String = "",
    val rewardId: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val rewardSparks: Int,
    val endDate: String,
    val active: Boolean = true
)

data class ChallengeSubmission(
    val id: String,
    val challengeId: String,
    val userId: String,
    val creationId: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class HubPortal(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val route: String = ""
)

data class Friendship(
    val id: String = "",
    val users: List<String> = emptyList(),
    val status: String = "pending",
    val requestSender: String = "",
    val createdAt: com.google.firebase.Timestamp? = null
)

data class Chat(
    val id: String = "",
    val members: List<String> = emptyList(),
    val lastMessageText: String = "",
    val updatedAt: com.google.firebase.Timestamp? = null
)

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)
data class JournalEntry(
    val id: String = "",
    val date: com.google.firebase.Timestamp? = null,
    val content: String = "",
    val imageUrl: String? = null,
    val mood: String? = null
)

data class SpotlightEntry(
    val id: String = "",
    val creationId: String = "",
    val featuredAt: com.google.firebase.Timestamp? = null,
    val curatorNote: String = ""
)

data class BlendedAppSuite(
    val id: String = "",
    val blendedName: String = "",
    val tagLine: String = "",
    val appIds: List<String> = emptyList(),
    val appNames: List<String> = emptyList(),
    val primaryHex: String = "#FF729F",
    val secondaryHex: String = "#8A4FFF",
    val createdDate: String = "Today",
    val sparksBonus: Int = 50
)

enum class DriveFileType(val displayName: String, val icon: String) {
    ARCHIVE("Muse ZIP Archive", "📦"),
    SPREADSHEET("Spreadsheet", "📊"),
    IMAGE("Moodboard Asset", "🖼️"),
    DOCUMENT("Formulation Spec", "📝"),
    DATA("Shade Data JSON", "🧪"),
    OTHER("Drive Resource", "📁")
}

data class MuseZipEntry(
    val name: String,
    val sizeBytes: Long = 0L,
    val category: String = "Formula Spec",
    val formulaName: String? = null
)

data class DriveFileItem(
    val id: String,
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val formattedSize: String,
    val modifiedDate: String,
    val fileType: DriveFileType,
    val webViewLink: String = "https://drive.google.com",
    val thumbnailLink: String? = null,
    val description: String? = null,
    val isImported: Boolean = false,
    val categoryTag: String = "Formula Spec",
    val extractedFormulaName: String? = null,
    val isArchive: Boolean = false,
    val archiveItemCount: Int = 0,
    val archiveEntries: List<MuseZipEntry> = emptyList()
)

data class DrivePullSummary(
    val totalFilesPulled: Int,
    val formulasCreated: Int,
    val moodboardsImported: Int,
    val specsProcessed: Int,
    val sparksEarned: Int,
    val importedFormulas: List<CustomCreation>,
    val archivesUnpacked: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)


