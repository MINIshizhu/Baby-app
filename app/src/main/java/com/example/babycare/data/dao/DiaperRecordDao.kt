package com.example.babycare.data.dao

import androidx.room.*
import com.example.babycare.data.entity.DiaperRecord
import kotlinx.coroutines.flow.Flow

/**
 * 排便记录数据访问接口
 */
@Dao
interface DiaperRecordDao {
    @Query("SELECT * FROM diaper_records WHERE babyId = :babyId ORDER BY time DESC")
    fun getDiaperRecordsByBaby(babyId: Long): Flow<List<DiaperRecord>>

    @Query("""
        SELECT * FROM diaper_records 
        WHERE babyId = :babyId 
        AND time BETWEEN :startTime AND :endTime 
        ORDER BY time DESC
    """)
    fun getDiaperRecordsByDateRange(
        babyId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<DiaperRecord>>

    @Query("""
        SELECT COUNT(*) FROM diaper_records 
        WHERE babyId = :babyId 
        AND time >= :startTime 
        AND type IN (0, 2)
    """)
    fun getTodayWetCount(babyId: Long, startTime: Long): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM diaper_records 
        WHERE babyId = :babyId 
        AND time >= :startTime 
        AND type IN (1, 2)
    """)
    fun getTodayDirtyCount(babyId: Long, startTime: Long): Flow<Int>

    @Insert
    suspend fun insert(record: DiaperRecord): Long

    @Update
    suspend fun update(record: DiaperRecord)

    @Delete
    suspend fun delete(record: DiaperRecord)

    @Query("DELETE FROM diaper_records WHERE babyId = :babyId")
    suspend fun deleteAllByBaby(babyId: Long)
} 