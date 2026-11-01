package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.room.SparkleItemEntity

/**
 * Social sharing helper using the Android ShareIntent (ACTION_SEND)
 * so fans can share their favorite 1D World sparkle items and collection milestones with friends on social media.
 */
object ShareUtil {

    /**
     * Shares an individual 1D sparkle collectible item with friends on WhatsApp, X, Instagram, Messages, etc.
     */
    fun shareSparkleItem(context: Context, item: SparkleItemEntity) {
        try {
            val shareText = buildString {
                append("✨ Look at my 1D World Sparkle Collectible! ✨\n\n")
                append("${item.iconEmoji} ${item.title}\n")
                append("👑 Member: ${item.memberName}\n")
                append("🌟 Rarity: ${item.rarity} • Value: ${item.sparkleValue} Sparks\n")
                append("🎶 Era: ${item.era}\n\n")
                append("\"${item.description}\"\n\n")
                append("Offline cached in 1D World • Forever Directioner ❤️ #OneDirection #1DWorld #DirectionerForever")
            }

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, "✨ 1D World Sparkle: ${item.title} (${item.memberName})")
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val chooser = Intent.createChooser(sendIntent, "Share '${item.title}' via").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback gracefully
        }
    }

    /**
     * Shares overall 1D sparkle collection progress and points summary.
     */
    fun shareCollectionMilestone(
        context: Context,
        collectedCount: Int,
        totalCount: Int,
        totalPoints: Int,
        topMember: String
    ) {
        try {
            val shareText = buildString {
                append("🏆 1D World Sparkle Vault Milestone! 🏆\n\n")
                append("✨ Collected: $collectedCount / $totalCount rare 1D artifacts\n")
                append("💎 Total Starlight Points: $totalPoints Sparks\n")
                append("❤️ Top Affinity: $topMember\n\n")
                append("Celebrating Liam, Harry, Louis, Niall & Zayn in 1D World! ✨\n")
                append("#OneDirection #1DWorld #1DSparkles #Directioner")
            }

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, "My 1D World Sparkle Collection ($collectedCount/$totalCount)")
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val chooser = Intent.createChooser(sendIntent, "Share 1D Milestone via").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback gracefully
        }
    }
}
