package com.example.babycare.data.repository

import com.example.babycare.data.dao.DiaperRecordDao
import com.example.babycare.data.entity.DiaperRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 排便记录仓库类
 *
 * @property diaperRecordDao 排便记录DAO
 */
@Singleton
class DiaperRecordRepository @Inject constructor(
    private val diaperRecordDao: DiaperRecordDao
) : BaseRepository<DiaperRecord, Long> {

    /**
     * 获取指定婴儿的所有排便记录
     * @param babyId 婴儿ID
     * @return Flow<List<DiaperRecord>> 排便记录列表流
     */
    fun getDiaperRecordsByBabyId(babyId: Long): Flow<List<DiaperRecord>> =
        diaperRecordDao.getDiaperRecordsByBabyId(babyId)

    /**
     * 获取指定日期范围内的排便记录
     * @param babyId 婴儿ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Flow<List<DiaperRecord>> 排便记录列表流
     */
    fun getDiaperRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<DiaperRecord>> {
        val startTime = startDate.toEpochSecond(ZoneOffset.UTC)
        val endTime = endDate.toEpochSecond(ZoneOffset.UTC)
        return diaperRecordDao.getDiaperRecordsByDateRange(babyId, startTime, endTime)
    }

    override suspend fun getById(id: Long): DiaperRecord? = null // 暂未实现

    override suspend fun insert(entity: DiaperRecord): Long =
        diaperRecordDao.insertDiaperRecord(entity)

    override suspend fun update(entity: DiaperRecord) =
        diaperRecordDao.updateDiaperRecord(entity)

    override suspend fun delete(entity: DiaperRecord) =
        diaperRecordDao.deleteDiaperRecord(entity)
} 