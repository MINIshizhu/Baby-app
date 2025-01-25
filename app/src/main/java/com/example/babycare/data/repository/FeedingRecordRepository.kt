package com.example.babycare.data.repository

import com.example.babycare.data.dao.FeedingRecordDao
import com.example.babycare.data.entity.FeedingRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 喂奶记录仓库类
 *
 * @property feedingRecordDao 喂奶记录DAO
 */
@Singleton
class FeedingRecordRepository @Inject constructor(
    private val feedingRecordDao: FeedingRecordDao
) : BaseRepository<FeedingRecord, Long> {

    /**
     * 获取指定婴儿的所有喂奶记录
     * @param babyId 婴儿ID
     * @return Flow<List<FeedingRecord>> 喂奶记录列表流
     */
    fun getFeedingRecordsByBabyId(babyId: Long): Flow<List<FeedingRecord>> =
        feedingRecordDao.getFeedingRecordsByBabyId(babyId)

    /**
     * 获取指定日期范围内的喂奶记录
     * @param babyId 婴儿ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Flow<List<FeedingRecord>> 喂奶记录列表流
     */
    fun getFeedingRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<FeedingRecord>> {
        val startTime = startDate.toEpochSecond(ZoneOffset.UTC)
        val endTime = endDate.toEpochSecond(ZoneOffset.UTC)
        return feedingRecordDao.getFeedingRecordsByDateRange(babyId, startTime, endTime)
    }

    override suspend fun getById(id: Long): FeedingRecord? = null // 暂未实现

    override suspend fun insert(entity: FeedingRecord): Long =
        feedingRecordDao.insertFeedingRecord(entity)

    override suspend fun update(entity: FeedingRecord) =
        feedingRecordDao.updateFeedingRecord(entity)

    override suspend fun delete(entity: FeedingRecord) =
        feedingRecordDao.deleteFeedingRecord(entity)
} 