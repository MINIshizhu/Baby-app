package com.example.babycare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 睡眠记录实体类
 *
 * @property id 主键ID
 * @property babyId 关联的婴儿ID
 * @property startTime 开始时间
 * @property endTime 结束时间
 * @property quality 睡眠质量 (0:差, 1:一般, 2:好)
 * @property note 备注
 */
@Entity(
    tableName = "sleep_records",
    foreignKeys = [
        ForeignKey(
            entity = Baby::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SleepRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val quality: Int? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) 