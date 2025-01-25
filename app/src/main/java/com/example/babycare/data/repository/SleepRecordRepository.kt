package com.example.babycare.data.repository

import com.example.babycare.data.dao.SleepRecordDao
import com.example.babycare.data.entity.SleepRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 睡眠记录仓库类
 *
 * @property sleepRecordDao 睡眠记录DAO
 */
@Singleton
class SleepRecordRepository @Inject constructor(
    private val sleepRecordDao: SleepRecordDao
) : BaseRepository<SleepRecord, Long> {

    /**
     * 获取指定婴儿的所有睡眠记录
     * @param babyId 婴儿ID
     * @return Flow<List<SleepRecord>> 睡眠记录列表流
     */
    fun getSleepRecordsByBabyId(babyId: Long): Flow<List<SleepRecord>> =
        sleepRecordDao.getSleepRecordsByBabyId(babyId)

    /**
     * 获取指定日期范围内的睡眠记录
     * @param babyId 婴儿ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Flow<List<SleepRecord>> 睡眠记录列表流
     */
    fun getSleepRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<SleepRecord>> {
        val startTime = startDate.toEpochSecond(ZoneOffset.UTC)
        val endTime = endDate.toEpochSecond(ZoneOffset.UTC)
        return sleepRecordDao.getSleepRecordsByDateRange(babyId, startTime, endTime)
    }

    override suspend fun getById(id: Long): SleepRecord? = null // 暂未实现

    override suspend fun insert(entity: SleepRecord): Long =
        sleepRecordDao.insertSleepRecord(entity)

    override suspend fun update(entity: SleepRecord) =
        sleepRecordDao.updateSleepRecord(entity)

    override suspend fun delete(entity: SleepRecord) =
        sleepRecordDao.deleteSleepRecord(entity)
} 