package com.example.babycare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 排便记录实体类
 *
 * @property id 主键ID
 * @property babyId 关联的婴儿ID
 * @property time 记录时间
 * @property type 类型 (0:尿尿, 1:便便, 2:都有)
 * @property color 便便颜色
 * @property amount 数量 (0:少量, 1:中量, 2:大量)
 * @property note 备注
 * @property createdAt 创建时间
 */
@Entity(
    tableName = "diaper_records",
    foreignKeys = [
        ForeignKey(
            entity = Baby::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DiaperRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val time: Long,
    val type: Int,
    val color: Int? = null,
    val amount: Int? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) 