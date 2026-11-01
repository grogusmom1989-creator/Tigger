package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.ProductCategory
import com.example.data.FinishType

@Entity(tableName = "community_formulas")
data class CommunityFormulaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val creatorName: String,
    val creatorHandle: String,
    val category: ProductCategory,
    val hexColor: String,
    val secondaryHex: String,
    val finish: FinishType,
    val scentNote: String,
    val upvotes: Int,
    val isLiked: Boolean,
    val tags: List<String>
)
