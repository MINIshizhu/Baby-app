package com.example.babycare.data.dao

import androidx.room.*
import com.example.babycare.data.entity.FeedingRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 喂奶记录数据访问接口
 */
@Dao
interface FeedingRecordDao {
    @Query("SELECT * FROM feeding_records WHERE babyId = :babyId ORDER BY startTime DESC")
    fun getFeedingRecordsByBaby(babyId: Long): Flow<List<FeedingRecord>>

    @Query("""
        SELECT * FROM feeding_records 
        WHERE babyId = :babyId 
        AND startTime BETWEEN :startTime AND :endTime 
        ORDER BY startTime DESC
    """)
    fun getFeedingRecordsByDateRange(
        babyId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<FeedingRecord>>

    @Query("""
        SELECT COUNT(*) FROM feeding_records 
        WHERE babyId = :babyId 
        AND startTime >= :startTime
    """)
    fun getTodayFeedingCount(babyId: Long, startTime: Long): Flow<Int>

    @Insert
    suspend fun insert(record: FeedingRecord): Long

    @Update
    suspend fun update(record: FeedingRecord)

    @Delete
    suspend fun delete(record: FeedingRecord)

    @Query("DELETE FROM feeding_records WHERE babyId = :babyId")
    suspend fun deleteAllByBaby(babyId: Long)
} 