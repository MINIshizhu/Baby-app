package com.example.babycare.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.babycare.data.AppDatabase
import com.example.babycare.data.dao.BabyDao
import com.example.babycare.data.dao.FeedingRecordDao
import com.example.babycare.data.dao.SleepRecordDao
import com.example.babycare.data.dao.DiaperRecordDao
import com.example.babycare.data.dao.MedicineRecordDao
import com.example.babycare.data.dao.WaterRecordDao
import com.example.babycare.data.dao.GrowthRecordDao
import com.example.babycare.data.converter.DateConverter
import com.example.babycare.data.migration.MIGRATION_1_2
import com.example.babycare.data.repository.BabyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供数据库实例
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        dateConverter: DateConverter
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "babycare.db"
        )
        .addTypeConverter(dateConverter)
        .addMigrations(MIGRATION_1_2)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 可以在这里添加初始数据
            }
        })
        .build()
    }

    @Provides
    @Singleton
    fun provideDateConverter(): DateConverter = DateConverter()

    /**
     * 提供婴儿信息DAO
     */
    @Provides
    @Singleton
    fun provideBabyDao(database: AppDatabase): BabyDao = database.babyDao()

    /**
     * 提供喂奶记录DAO
     */
    @Provides
    fun provideFeedingRecordDao(database: AppDatabase): FeedingRecordDao = 
        database.feedingRecordDao()

    /**
     * 提供睡眠记录DAO
     */
    @Provides
    fun provideSleepRecordDao(database: AppDatabase): SleepRecordDao = 
        database.sleepRecordDao()

    /**
     * 提供排便记录DAO
     */
    @Provides
    fun provideDiaperRecordDao(database: AppDatabase): DiaperRecordDao = 
        database.diaperRecordDao()

    /**
     * 提供喂药记录DAO
     */
    @Provides
    fun provideMedicineRecordDao(database: AppDatabase): MedicineRecordDao = 
        database.medicineRecordDao()

    /**
     * 提供喂水记录DAO
     */
    @Provides
    fun provideWaterRecordDao(database: AppDatabase): WaterRecordDao = 
        database.waterRecordDao()

    /**
     * 提供成长记录DAO
     */
    @Provides
    fun provideGrowthRecordDao(database: AppDatabase): GrowthRecordDao = 
        database.growthRecordDao()

    @Provides
    @Singleton
    fun provideBabyRepository(
        babyDao: BabyDao
    ): BabyRepository = BabyRepository(babyDao)
} 