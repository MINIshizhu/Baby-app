package com.example.babycare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 喂药记录实体类
 *
 * @property id 主键ID
 * @property babyId 关联的婴儿ID
 * @property time 用药时间
 * @property medicineName 药品名称
 * @property dosage 剂量
 * @property unit 单位(如ml、片等)
 * @property note 备注
 * @property reminderTime 下次提醒时间
 */
@Entity(
    tableName = "medicine_records",
    foreignKeys = [
        ForeignKey(
            entity = Baby::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MedicineRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val time: Long,
    val medicineName: String,
    val dosage: Float,
    val unit: String,
    val note: String? = null,
    val reminderTime: Long? = null
) 