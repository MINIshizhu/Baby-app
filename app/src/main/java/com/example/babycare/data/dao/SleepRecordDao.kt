package com.example.babycare.data.dao

import androidx.room.*
import com.example.babycare.data.entity.SleepRecord
import kotlinx.coroutines.flow.Flow

/**
 * 睡眠记录数据访问接口
 */
@Dao
interface SleepRecordDao {
    @Query("SELECT * FROM sleep_records WHERE babyId = :babyId ORDER BY startTime DESC")
    fun getSleepRecordsByBaby(babyId: Long): Flow<List<SleepRecord>>

    @Query("""
        SELECT * FROM sleep_records 
        WHERE babyId = :babyId 
        AND startTime BETWEEN :startTime AND :endTime 
        ORDER BY startTime DESC
    """)
    fun getSleepRecordsByDateRange(
        babyId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<SleepRecord>>

    @Query("""
        SELECT SUM(CASE 
            WHEN endTime IS NOT NULL THEN endTime - startTime 
            ELSE 0 
        END) 
        FROM sleep_records 
        WHERE babyId = :babyId 
        AND startTime >= :startTime
    """)
    fun getTodaySleepDuration(babyId: Long, startTime: Long): Flow<Long>

    @Insert
    suspend fun insert(record: SleepRecord): Long

    @Update
    suspend fun update(record: SleepRecord)

    @Delete
    suspend fun delete(record: SleepRecord)

    @Query("DELETE FROM sleep_records WHERE babyId = :babyId")
    suspend fun deleteAllByBaby(babyId: Long)
} 