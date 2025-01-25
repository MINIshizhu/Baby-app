package com.example.babycare.data.repository

import com.example.babycare.data.dao.BabyDao
import com.example.babycare.data.entity.Baby
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 宝宝信息仓库
 */
@Singleton
class BabyRepository @Inject constructor(
    private val babyDao: BabyDao
) {
    /**
     * 获取所有宝宝信息
     */
    fun getAllBabies(): Flow<List<Baby>> = babyDao.getAllBabies()

    /**
     * 获取当前选中的宝宝
     */
    fun getCurrentBaby(): Flow<Baby?> = babyDao.getCurrentBaby()

    /**
     * 根据ID获取宝宝信息
     */
    suspend fun getBabyById(id: Long): Baby? = babyDao.getBabyById(id)

    /**
     * 添加宝宝信息
     */
    suspend fun insertBaby(baby: Baby): Long = babyDao.insert(baby)

    /**
     * 更新宝宝信息
     */
    suspend fun updateBaby(baby: Baby) = babyDao.update(baby)

    /**
     * 删除宝宝信息
     */
    suspend fun deleteBaby(baby: Baby) = babyDao.delete(baby)

    /**
     * 设置当前选中的宝宝
     */
    suspend fun setCurrentBaby(babyId: Long) = babyDao.setCurrentBaby(babyId)
} 