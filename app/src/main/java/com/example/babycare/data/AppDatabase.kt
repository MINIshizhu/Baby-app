package com.example.babycare.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.babycare.data.dao.BabyDao
import com.example.babycare.data.dao.FeedingRecordDao
import com.example.babycare.data.dao.SleepRecordDao
import com.example.babycare.data.dao.DiaperRecordDao
import com.example.babycare.data.dao.MedicineRecordDao
import com.example.babycare.data.dao.WaterRecordDao
import com.example.babycare.data.dao.GrowthRecordDao
import com.example.babycare.data.dao.VaccineRecordDao
import com.example.babycare.data.entity.Baby
import com.example.babycare.data.entity.FeedingRecord
import com.example.babycare.data.entity.SleepRecord
import com.example.babycare.data.entity.DiaperRecord
import com.example.babycare.data.entity.MedicineRecord
import com.example.babycare.data.entity.WaterRecord
import com.example.babycare.data.entity.GrowthRecord
import com.example.babycare.data.entity.VaccineRecord
import com.example.babycare.data.converter.DateConverter
import com.example.babycare.data.converters.DateTimeConverter

/**
 * 应用数据库
 */
@Database(
    entities = [
        Baby::class,
        FeedingRecord::class,
        SleepRecord::class,
        DiaperRecord::class,
        GrowthRecord::class,
        VaccineRecord::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class, DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun feedingRecordDao(): FeedingRecordDao
    abstract fun sleepRecordDao(): SleepRecordDao
    abstract fun diaperRecordDao(): DiaperRecordDao
    abstract fun growthRecordDao(): GrowthRecordDao
    abstract fun vaccineRecordDao(): VaccineRecordDao
} 