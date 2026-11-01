package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an offline-cached '1D World' sparkle collectible item.
 * Enables fans to view and manage their sparkle collection without an active internet connection.
 */
@Entity(tableName = "sparkle_items")
data class SparkleItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val memberName: String, // "Band", "Liam", "Harry", "Louis", "Niall", "Zayn"
    val rarity: String,     // "Common", "Rare", "Super Rare", "Legendary"
    val iconEmoji: String,
    val era: String,
    val colorHex: String,
    val description: String,
    val sparkleValue: Int,
    val isCollected: Boolean = false,
    val collectedTimestamp: Long = 0L
)
