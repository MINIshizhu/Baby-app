package com.example.babycare.data.repository

import com.example.babycare.data.dao.MedicineRecordDao
import com.example.babycare.data.entity.MedicineRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 喂药记录仓库类
 *
 * @property medicineRecordDao 喂药记录DAO
 */
@Singleton
class MedicineRecordRepository @Inject constructor(
    private val medicineRecordDao: MedicineRecordDao
) : BaseRepository<MedicineRecord, Long> {

    /**
     * 获取指定婴儿的所有喂药记录
     * @param babyId 婴儿ID
     * @return Flow<List<MedicineRecord>> 喂药记录列表流
     */
    fun getMedicineRecordsByBabyId(babyId: Long): Flow<List<MedicineRecord>> =
        medicineRecordDao.getMedicineRecordsByBabyId(babyId)

    /**
     * 获取指定日期范围内的喂药记录
     * @param babyId 婴儿ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Flow<List<MedicineRecord>> 喂药记录列表流
     */
    fun getMedicineRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<MedicineRecord>> {
        val startTime = startDate.toEpochSecond(ZoneOffset.UTC)
        val endTime = endDate.toEpochSecond(ZoneOffset.UTC)
        return medicineRecordDao.getMedicineRecordsByDateRange(babyId, startTime, endTime)
    }

    /**
     * 获取未来的提醒
     * @return Flow<List<MedicineRecord>> 待提醒的喂药记录列表流
     */
    fun getUpcomingReminders(): Flow<List<MedicineRecord>> =
        medicineRecordDao.getUpcomingReminders(System.currentTimeMillis())

    override suspend fun getById(id: Long): MedicineRecord? = null // 暂未实现

    override suspend fun insert(entity: MedicineRecord): Long =
        medicineRecordDao.insertMedicineRecord(entity)

    override suspend fun update(entity: MedicineRecord) =
        medicineRecordDao.updateMedicineRecord(entity)

    override suspend fun delete(entity: MedicineRecord) =
        medicineRecordDao.deleteMedicineRecord(entity)
} 