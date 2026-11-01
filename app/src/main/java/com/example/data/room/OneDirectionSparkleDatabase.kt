package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database dedicated to 1D World offline caching, completely separated from Kiki's Clean Beauty database.
 */
@Database(entities = [SparkleItemEntity::class], version = 1, exportSchema = false)
abstract class OneDirectionSparkleDatabase : RoomDatabase() {
    abstract fun sparkleDao(): SparkleDao

    companion object {
        @Volatile
        private var INSTANCE: OneDirectionSparkleDatabase? = null

        fun getDatabase(context: Context): OneDirectionSparkleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OneDirectionSparkleDatabase::class.java,
                    "onedirection_sparkle_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
