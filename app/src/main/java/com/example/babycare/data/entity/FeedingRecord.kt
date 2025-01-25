package com.example.babycare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 喂奶记录实体类
 *
 * @property id 主键ID
 * @property babyId 关联的婴儿ID
 * @property startTime 开始时间
 * @property endTime 结束时间
 * @property type 喂养类型 (0:母乳, 1:奶瓶)
 * @property amount 奶量(ml)，仅瓶喂时有效
 * @property side 喂养侧 (0:左侧, 1:右侧)
 * @property note 备注
 * @property createdAt 创建时间
 */
@Entity(
    tableName = "feeding_records",
    foreignKeys = [
        ForeignKey(
            entity = Baby::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FeedingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val type: Int,
    val amount: Int? = null,
    val side: Int? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) 