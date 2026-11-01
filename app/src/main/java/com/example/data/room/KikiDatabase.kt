package com.example.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.FinishType
import com.example.data.ProductCategory
import com.example.util.Converters

@Database(entities = [CommunityFormulaEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class KikiDatabase : RoomDatabase() {
    abstract fun communityFormulaDao(): CommunityFormulaDao
}
