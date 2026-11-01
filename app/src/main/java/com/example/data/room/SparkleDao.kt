package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for 1D World sparkle collectibles cached locally with Room.
 */
@Dao
interface SparkleDao {
    @Query("SELECT * FROM sparkle_items ORDER BY sparkleValue DESC")
    fun getAllSparkles(): Flow<List<SparkleItemEntity>>

    @Query("SELECT * FROM sparkle_items WHERE memberName = :memberName ORDER BY sparkleValue DESC")
    fun getSparklesByMember(memberName: String): Flow<List<SparkleItemEntity>>

    @Query("SELECT * FROM sparkle_items WHERE isCollected = 1 ORDER BY collectedTimestamp DESC")
    fun getCollectedSparkles(): Flow<List<SparkleItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SparkleItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSparkle(item: SparkleItemEntity)

    @Query("UPDATE sparkle_items SET isCollected = :isCollected, collectedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateCollectedStatus(id: String, isCollected: Boolean, timestamp: Long)

    @Query("SELECT COUNT(*) FROM sparkle_items")
    suspend fun getItemCount(): Int

    @Query("SELECT COUNT(*) FROM sparkle_items WHERE isCollected = 1")
    fun getCollectedCount(): Flow<Int>

    @Query("SELECT SUM(sparkleValue) FROM sparkle_items WHERE isCollected = 1")
    fun getTotalCollectedSparklePoints(): Flow<Int?>
}
