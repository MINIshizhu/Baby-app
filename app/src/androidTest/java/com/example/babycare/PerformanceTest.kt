package com.example.babycare

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PerformanceTest {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun measureDatabaseOperation() {
        benchmarkRule.measureRepeated {
            // 测试数据库操作性能
        }
    }
} 