package com.example.babycare.data.repository

/**
 * Repository基础接口
 *
 * @param T 实体类型
 * @param ID 主键类型
 */
interface BaseRepository<T, ID> {
    suspend fun getById(id: ID): T?
    suspend fun insert(entity: T): ID
    suspend fun update(entity: T)
    suspend fun delete(entity: T)
} 