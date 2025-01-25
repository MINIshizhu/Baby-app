package com.example.babycare.data.dao

import androidx.room.*
import com.example.babycare.data.entity.Baby
import kotlinx.coroutines.flow.Flow

/**
 * 婴儿信息数据访问接口
 */
@Dao
interface BabyDao {
    @Query("SELECT * FROM babies ORDER BY createdAt DESC")
    fun getAllBabies(): Flow<List<Baby>>

    @Query("SELECT * FROM babies WHERE isSelected = 1 LIMIT 1")
    fun getCurrentBaby(): Flow<Baby?>

    @Query("SELECT * FROM babies WHERE id = :id")
    suspend fun getBabyById(id: Long): Baby?

    @Insert
    suspend fun insert(baby: Baby): Long

    @Update
    suspend fun update(baby: Baby)

    @Delete
    suspend fun delete(baby: Baby)

    @Transaction
    suspend fun setCurrentBaby(babyId: Long) {
        // 先取消所有选中状态
        updateAllBabiesSelection(false)
        // 设置新的选中状态
        updateBabySelection(babyId, true)
    }

    @Query("UPDATE babies SET isSelected = :isSelected")
    suspend fun updateAllBabiesSelection(isSelected: Boolean)

    @Query("UPDATE babies SET isSelected = :isSelected WHERE id = :babyId")
    suspend fun updateBabySelection(babyId: Long, isSelected: Boolean)
} 