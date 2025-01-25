package com.example.babycare.data.dao

import androidx.room.*
import com.example.babycare.data.entity.GrowthRecord
import kotlinx.coroutines.flow.Flow

/**
 * 成长记录数据访问接口
 */
@Dao
interface GrowthRecordDao {
    @Query("SELECT * FROM growth_records WHERE babyId = :babyId ORDER BY time DESC")
    fun getGrowthRecordsByBabyId(babyId: Long): Flow<List<GrowthRecord>>

    @Query("""
        SELECT * FROM growth_records 
        WHERE babyId = :babyId 
        AND time BETWEEN :startTime AND :endTime 
        ORDER BY time DESC
    """)
    fun getGrowthRecordsByDateRange(
        babyId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<GrowthRecord>>

    @Query("SELECT * FROM growth_records WHERE babyId = :babyId ORDER BY time DESC LIMIT 1")
    fun getLatestGrowthRecord(babyId: Long): Flow<GrowthRecord?>

    @Insert
    suspend fun insertGrowthRecord(record: GrowthRecord): Long

    @Update
    suspend fun updateGrowthRecord(record: GrowthRecord)

    @Delete
    suspend fun deleteGrowthRecord(record: GrowthRecord)
} 