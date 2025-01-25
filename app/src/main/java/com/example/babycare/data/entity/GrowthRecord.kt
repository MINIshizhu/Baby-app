package com.example.babycare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 成长记录实体类
 *
 * @property id 主键ID
 * @property babyId 关联的婴儿ID
 * @property time 记录时间
 * @property height 身高(cm)
 * @property weight 体重(g)
 * @property headCircumference 头围(cm)
 * @property milestone 里程碑事件
 * @property note 备注
 */
@Entity(
    tableName = "growth_records",
    foreignKeys = [
        ForeignKey(
            entity = Baby::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GrowthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val time: Long,
    val height: Float? = null, // 单位：cm
    val weight: Float? = null, // 单位：kg
    val headCircumference: Float? = null, // 单位：cm
    val milestone: String? = null, // 里程碑事件
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) 