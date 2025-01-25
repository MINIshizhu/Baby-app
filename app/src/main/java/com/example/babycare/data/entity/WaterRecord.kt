package com.example.babycare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 喂水记录实体类
 *
 * @property id 主键ID
 * @property babyId 关联的婴儿ID
 * @property time 喂水时间
 * @property amount 水量(ml)
 * @property temperature 水温 (0:常温, 1:温, 2:热)
 * @property note 备注
 */
@Entity(
    tableName = "water_records",
    foreignKeys = [
        ForeignKey(
            entity = Baby::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WaterRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val time: Long,
    val amount: Int,
    val temperature: Int = 0,
    val note: String? = null
) 