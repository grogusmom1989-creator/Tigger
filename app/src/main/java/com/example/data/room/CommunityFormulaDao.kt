package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityFormulaDao {
    @Query("SELECT * FROM community_formulas")
    fun getAllFormulas(): Flow<List<CommunityFormulaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormulas(formulas: List<CommunityFormulaEntity>)

    @Query("DELETE FROM community_formulas")
    suspend fun deleteAll()
}
