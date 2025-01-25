package com.example.babycare.data.dao

import androidx.room.*
import com.example.babycare.data.entity.MedicineRecord
import kotlinx.coroutines.flow.Flow

/**
 * 喂药记录数据访问接口
 */
@Dao
interface MedicineRecordDao {
    @Query("SELECT * FROM medicine_records WHERE babyId = :babyId ORDER BY time DESC")
    fun getMedicineRecordsByBabyId(babyId: Long): Flow<List<MedicineRecord>>

    @Query("""
        SELECT * FROM medicine_records 
        WHERE babyId = :babyId 
        AND time BETWEEN :startTime AND :endTime 
        ORDER BY time DESC
    """)
    fun getMedicineRecordsByDateRange(
        babyId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<MedicineRecord>>

    @Query("SELECT * FROM medicine_records WHERE reminderTime IS NOT NULL AND reminderTime > :now")
    fun getUpcomingReminders(now: Long): Flow<List<MedicineRecord>>

    @Insert
    suspend fun insertMedicineRecord(record: MedicineRecord): Long

    @Update
    suspend fun updateMedicineRecord(record: MedicineRecord)

    @Delete
    suspend fun deleteMedicineRecord(record: MedicineRecord)
} 