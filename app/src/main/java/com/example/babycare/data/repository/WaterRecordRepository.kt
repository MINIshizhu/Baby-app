package com.example.babycare.data.repository

import com.example.babycare.data.dao.WaterRecordDao
import com.example.babycare.data.entity.WaterRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 喂水记录仓库类
 *
 * @property waterRecordDao 喂水记录DAO
 */
@Singleton
class WaterRecordRepository @Inject constructor(
    private val waterRecordDao: WaterRecordDao
) : BaseRepository<WaterRecord, Long> {

    /**
     * 获取指定婴儿的所有喂水记录
     * @param babyId 婴儿ID
     * @return Flow<List<WaterRecord>> 喂水记录列表流
     */
    fun getWaterRecordsByBabyId(babyId: Long): Flow<List<WaterRecord>> =
        waterRecordDao.getWaterRecordsByBabyId(babyId)

    /**
     * 获取指定日期范围内的喂水记录
     * @param babyId 婴儿ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Flow<List<WaterRecord>> 喂水记录列表流
     */
    fun getWaterRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<WaterRecord>> {
        val startTime = startDate.toEpochSecond(ZoneOffset.UTC)
        val endTime = endDate.toEpochSecond(ZoneOffset.UTC)
        return waterRecordDao.getWaterRecordsByDateRange(babyId, startTime, endTime)
    }

    /**
     * 获取指定日期范围内的总喂水量
     * @param babyId 婴儿ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Flow<Int?> 总喂水量流
     */
    fun getTotalWaterAmount(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<Int?> {
        val startTime = startDate.toEpochSecond(ZoneOffset.UTC)
        val endTime = endDate.toEpochSecond(ZoneOffset.UTC)
        return waterRecordDao.getTotalWaterAmount(babyId, startTime, endTime)
    }

    override suspend fun getById(id: Long): WaterRecord? = null // 暂未实现

    override suspend fun insert(entity: WaterRecord): Long =
        waterRecordDao.insertWaterRecord(entity)

    override suspend fun update(entity: WaterRecord) =
        waterRecordDao.updateWaterRecord(entity)

    override suspend fun delete(entity: WaterRecord) =
        waterRecordDao.deleteWaterRecord(entity)
} 