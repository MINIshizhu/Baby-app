package com.example.babycare

import android.app.Application
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BabyCareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            setupDebugTools()
        }
    }

    private fun setupDebugTools() {
        // 初始化 Stetho
        Stetho.initializeWithDefaults(this)
        
        // 初始化 LeakCanary (不需要手动初始化，但可以配置)
        LeakCanary.config = LeakCanary.config.copy(
            retainedVisibleThreshold = 3
        )
    }
} 