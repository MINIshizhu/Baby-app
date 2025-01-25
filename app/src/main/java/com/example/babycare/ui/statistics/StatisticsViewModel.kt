package com.example.babycare.ui.statistics

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.babycare.data.repository.*
import com.example.babycare.ui.base.BaseViewModel
import com.example.babycare.utils.ChartGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.*
import javax.inject.Inject

/**
 * 统计页面视图状态
 */
data class StatisticsViewState(
    val selectedBabyId: Long? = null,
    val selectedPeriod: StatisticsPeriod = StatisticsPeriod.TODAY,
    val feedingStats: FeedingStats = FeedingStats(),
    val sleepStats: SleepStats = SleepStats(),
    val diaperStats: DiaperStats = DiaperStats(),
    val waterStats: WaterStats = WaterStats(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val feedingTimeDistribution: Bitmap? = null,
    val feedingAccumulation: Bitmap? = null,
    val periodComparison: Bitmap? = null
)

/**
 * 统计页面事件
 */
sealed class StatisticsEvent {
    data class SelectBaby(val babyId: Long) : StatisticsEvent()
    data class SelectPeriod(val period: StatisticsPeriod) : StatisticsEvent()
    data class UpdatePeriodComparison(val metric: FeedingMetric) : StatisticsEvent()
}

enum class StatisticsPeriod {
    TODAY, WEEK, MONTH
}

// 各类统计数据模型
data class FeedingStats(
    val totalCount: Int = 0,
    val totalAmount: Int = 0,
    val averageDuration: Int = 0
)

data class SleepStats(
    val totalDuration: Int = 0,
    val count: Int = 0,
    val averageDuration: Float = 0f
)

data class DiaperStats(
    val totalCount: Int = 0,
    val wetCount: Int = 0,
    val dirtyCount: Int = 0
)

data class WaterStats(
    val totalAmount: Int = 0,
    val count: Int = 0,
    val averageAmount: Float = 0f
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val feedingRepository: FeedingRecordRepository,
    private val sleepRepository: SleepRecordRepository,
    private val diaperRepository: DiaperRecordRepository,
    private val waterRepository: WaterRecordRepository,
    private val chartGenerator: ChartGenerator
) : BaseViewModel<StatisticsViewState, StatisticsEvent>() {

    override fun initViewState() = StatisticsViewState()

    init {
        // 假设有一个默认的婴儿ID
        handleEvent(StatisticsEvent.SelectBaby(1L))
    }

    override fun handleEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.SelectBaby -> {
                updateState { it.copy(selectedBabyId = event.babyId) }
                loadStatistics()
            }
            is StatisticsEvent.SelectPeriod -> {
                updateState { it.copy(selectedPeriod = event.period) }
                loadStatistics()
            }
            is StatisticsEvent.UpdatePeriodComparison -> {
                // Handle the event
            }
        }
    }

    private fun loadStatistics() {
        val babyId = viewState.value.selectedBabyId ?: return
        val (startTime, endTime) = getTimeRange(viewState.value.selectedPeriod)
        val (previousStart, previousEnd) = getPreviousPeriodRange(viewState.value.selectedPeriod)

        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }

            try {
                // 加载基本统计数据
                combine(
                    feedingRepository.getFeedingRecordsByDateRange(babyId, startTime, endTime),
                    feedingRepository.getFeedingRecordsByDateRange(babyId, previousStart, previousEnd)
                ) { currentRecords, previousRecords ->
                    // 生成时间分布散点图
                    val timeDistribution = chartGenerator.generateFeedingScatterChart(currentRecords)
                    
                    // 生成累积喂奶量区域图
                    val accumulation = chartGenerator.generateFeedingAreaChart(currentRecords)
                    
                    // 生成时间段对比图
                    val comparison = chartGenerator.generatePeriodComparisonChart(
                        currentRecords,
                        previousRecords,
                        FeedingMetric.AMOUNT
                    )

                    updateState { state ->
                        state.copy(
                            feedingTimeDistribution = timeDistribution,
                            feedingAccumulation = accumulation,
                            periodComparison = comparison,
                            isLoading = false
                        )
                    }
                }.catch { error ->
                    updateState { it.copy(error = error.message, isLoading = false) }
                }.collect()
            } catch (e: Exception) {
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun getTimeRange(period: StatisticsPeriod): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()
        val start = when (period) {
            StatisticsPeriod.TODAY -> now.with(LocalTime.MIN)
            StatisticsPeriod.WEEK -> now.with(LocalTime.MIN).minusWeeks(1)
            StatisticsPeriod.MONTH -> now.with(LocalTime.MIN).minusMonths(1)
        }
        return start to now
    }

    private fun getPreviousPeriodRange(period: StatisticsPeriod): Pair<LocalDateTime, LocalDateTime> {
        val (start, end) = getTimeRange(period)
        val duration = java.time.Duration.between(start, end)
        return start.minus(duration) to start
    }

    private fun calculateFeedingStats(
        babyId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Flow<FeedingStats> = flow {
        feedingRepository.getFeedingRecordsByDateRange(babyId, startTime, endTime)
            .collect { records ->
                val totalCount = records.size
                val totalAmount = records.sumOf { it.amount ?: 0 }
                val totalDuration = records.sumOf { record ->
                    if (record.endTime != null) {
                        (record.endTime - record.startTime).toInt()
                    } else 0
                }
                val averageDuration = if (totalCount > 0) totalDuration / totalCount else 0

                emit(FeedingStats(
                    totalCount = totalCount,
                    totalAmount = totalAmount,
                    averageDuration = averageDuration
                ))
            }
    }

    // 其他统计计算函数...

    private fun generateCharts(data: ExportData): List<Bitmap> {
        return listOf(
            // 生成成长趋势图
            chartGenerator.generateGrowthChart(data.growthRecords),
            
            // 生成喂奶统计图
            chartGenerator.generateFeedingChart(data.feedingRecords),
            
            // 生成睡眠统计图
            chartGenerator.generateSleepChart(data.sleepRecords),
            
            // 生成记录分布图
            chartGenerator.generateRecordDistributionChart(
                feedingCount = data.feedingRecords.size,
                sleepCount = data.sleepRecords.size,
                diaperCount = data.diaperRecords.size,
                medicineCount = data.medicineRecords.size,
                waterCount = data.waterRecords.size
            )
        )
    }
} 