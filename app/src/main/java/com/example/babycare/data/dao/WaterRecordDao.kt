package com.example.babycare.data.dao

import androidx.room.*
import com.example.babycare.data.entity.WaterRecord
import kotlinx.coroutines.flow.Flow

/**
 * 喂水记录数据访问接口
 */
@Dao
interface WaterRecordDao {
    @Query("SELECT * FROM water_records WHERE babyId = :babyId ORDER BY time DESC")
    fun getWaterRecordsByBabyId(babyId: Long): Flow<List<WaterRecord>>

    @Query("""
        SELECT * FROM water_records 
        WHERE babyId = :babyId 
        AND time BETWEEN :startTime AND :endTime 
        ORDER BY time DESC
    """)
    fun getWaterRecordsByDateRange(
        babyId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<WaterRecord>>

    @Query("SELECT SUM(amount) FROM water_records WHERE babyId = :babyId AND time BETWEEN :startTime AND :endTime")
    fun getTotalWaterAmount(babyId: Long, startTime: Long, endTime: Long): Flow<Int?>

    @Insert
    suspend fun insertWaterRecord(record: WaterRecord): Long

    @Update
    suspend fun updateWaterRecord(record: WaterRecord)

    @Delete
    suspend fun deleteWaterRecord(record: WaterRecord)
} 