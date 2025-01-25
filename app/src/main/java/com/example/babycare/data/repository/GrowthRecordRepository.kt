package com.example.babycare.data.repository

import com.example.babycare.data.dao.GrowthRecordDao
import com.example.babycare.data.entity.GrowthRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 成长记录仓库类
 *
 * @property growthRecordDao 成长记录DAO
 */
@Singleton
class GrowthRecordRepository @Inject constructor(
    private val growthRecordDao: GrowthRecordDao
) : BaseRepository<GrowthRecord, Long> {

    /**
     * 获取指定婴儿的所有成长记录
     * @param babyId 婴儿ID
     * @return Flow<List<GrowthRecord>> 成长记录列表流
     */
    fun getGrowthRecordsByBabyId(babyId: Long): Flow<List<GrowthRecord>> =
        growthRecordDao.getGrowthRecordsByBabyId(babyId)

    /**
     * 获取指定日期范围内的成长记录
     * @param babyId 婴儿ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Flow<List<GrowthRecord>> 成长记录列表流
     */
    fun getGrowthRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<GrowthRecord>> {
        val startTime = startDate.toEpochSecond(ZoneOffset.UTC)
        val endTime = endDate.toEpochSecond(ZoneOffset.UTC)
        return growthRecordDao.getGrowthRecordsByDateRange(babyId, startTime, endTime)
    }

    /**
     * 获取最新的成长记录
     * @param babyId 婴儿ID
     * @return Flow<GrowthRecord?> 最新成长记录流
     */
    fun getLatestGrowthRecord(babyId: Long): Flow<GrowthRecord?> =
        growthRecordDao.getLatestGrowthRecord(babyId)

    override suspend fun getById(id: Long): GrowthRecord? = null // 暂未实现

    override suspend fun insert(entity: GrowthRecord): Long =
        growthRecordDao.insertGrowthRecord(entity)

    override suspend fun update(entity: GrowthRecord) =
        growthRecordDao.updateGrowthRecord(entity)

    override suspend fun delete(entity: GrowthRecord) =
        growthRecordDao.deleteGrowthRecord(entity)
} 