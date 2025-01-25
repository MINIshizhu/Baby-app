package com.example.babycare.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * 婴儿信息实体类
 *
 * @property id 主键ID
 * @property name 婴儿姓名
 * @property gender 性别 (0:女, 1:男)
 * @property birthday 出生日期
 * @property avatar 头像存储路径
 * @property createdAt 创建时间
 */
@Entity(tableName = "babies")
data class Baby(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: Int, // 0:女孩 1:男孩
    val birthday: Long,
    val avatar: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSelected: Boolean = false
) 