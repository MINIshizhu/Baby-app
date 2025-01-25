package com.example.babycare.ui.growth

import androidx.lifecycle.viewModelScope
import com.example.babycare.data.entity.GrowthRecord
import com.example.babycare.data.repository.GrowthRecordRepository
import com.example.babycare.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * 成长记录视图状态
 */
data class GrowthViewState(
    val selectedBabyId: Long? = null,
    val selectedPeriod: GrowthPeriod = GrowthPeriod.MONTH_3,
    val latestRecord: GrowthRecordUI? = null,
    val growthRecords: List<GrowthRecordUI> = emptyList(),
    val growthTrends: GrowthTrends = GrowthTrends(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 成长记录事件
 */
sealed class GrowthEvent {
    data class SelectBaby(val babyId: Long) : GrowthEvent()
    data class SelectPeriod(val period: GrowthPeriod) : GrowthEvent()
    data class AddGrowthRecord(
        val height: Float?,
        val weight: Float?,
        val headCircumference: Float?
    ) : GrowthEvent()
    data class UpdateGrowthRecord(val record: GrowthRecordUI) : GrowthEvent()
    data class DeleteGrowthRecord(val recordId: Long) : GrowthEvent()
}

enum class GrowthPeriod {
    MONTH_3, MONTH_6, YEAR_1
}

/**
 * 成长趋势数据
 */
data class GrowthTrends(
    val heightGrowth: Float = 0f,    // 身高增长(cm)
    val weightGain: Float = 0f,      // 体重增长(kg)
    val headGrowth: Float = 0f,      // 头围增长(cm)
    val period: Int = 3              // 统计周期(月)
)

/**
 * UI数据模型
 */
data class GrowthRecordUI(
    val id: Long,
    val time: String,
    val height: Float?,
    val weight: Float?,
    val headCircumference: Float?
)

@HiltViewModel
class GrowthViewModel @Inject constructor(
    private val growthRepository: GrowthRecordRepository
) : BaseViewModel<GrowthViewState, GrowthEvent>() {

    override fun initViewState() = GrowthViewState()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        handleEvent(GrowthEvent.SelectBaby(1L)) // 假设默认婴儿ID为1
    }

    override fun handleEvent(event: GrowthEvent) {
        when (event) {
            is GrowthEvent.SelectBaby -> {
                updateState { it.copy(selectedBabyId = event.babyId) }
                loadGrowthData()
            }
            is GrowthEvent.SelectPeriod -> {
                updateState { it.copy(selectedPeriod = event.period) }
                loadGrowthData()
            }
            is GrowthEvent.AddGrowthRecord -> addGrowthRecord(event)
            is GrowthEvent.UpdateGrowthRecord -> updateGrowthRecord(event.record)
            is GrowthEvent.DeleteGrowthRecord -> deleteGrowthRecord(event.recordId)
        }
    }

    private fun loadGrowthData() {
        val babyId = viewState.value.selectedBabyId ?: return
        val (startTime, endTime) = getTimeRange(viewState.value.selectedPeriod)

        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }

            try {
                // 并行加载最新记录和历史记录
                combine(
                    growthRepository.getLatestGrowthRecord(babyId),
                    growthRepository.getGrowthRecordsByDateRange(babyId, startTime, endTime)
                ) { latest, records ->
                    val sortedRecords = records.sortedByDescending { it.time }
                    val trends = calculateGrowthTrends(sortedRecords)
                    
                    updateState { state ->
                        state.copy(
                            latestRecord = latest?.toUI(),
                            growthRecords = sortedRecords.map { it.toUI() },
                            growthTrends = trends,
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

    private fun addGrowthRecord(event: GrowthEvent.AddGrowthRecord) {
        val babyId = viewState.value.selectedBabyId ?: return
        
        viewModelScope.launch {
            try {
                val record = GrowthRecord(
                    babyId = babyId,
                    time = System.currentTimeMillis(),
                    height = event.height,
                    weight = event.weight,
                    headCircumference = event.headCircumference
                )
                growthRepository.insert(record)
                loadGrowthData() // 重新加载数据以更新UI
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun updateGrowthRecord(record: GrowthRecordUI) {
        viewModelScope.launch {
            try {
                growthRepository.update(record.toEntity(viewState.value.selectedBabyId ?: return@launch))
                loadGrowthData()
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun deleteGrowthRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                growthRepository.delete(GrowthRecord(
                    id = recordId,
                    babyId = viewState.value.selectedBabyId ?: return@launch,
                    time = 0
                ))
                loadGrowthData()
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun calculateGrowthTrends(records: List<GrowthRecord>): GrowthTrends {
        if (records.size < 2) return GrowthTrends()

        val latest = records.first()
        val oldest = records.last()

        return GrowthTrends(
            heightGrowth = (latest.height ?: 0f) - (oldest.height ?: 0f),
            weightGain = (latest.weight ?: 0f) - (oldest.weight ?: 0f),
            headGrowth = (latest.headCircumference ?: 0f) - (oldest.headCircumference ?: 0f),
            period = viewState.value.selectedPeriod.toPeriodMonths()
        )
    }

    private fun getTimeRange(period: GrowthPeriod): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()
        val start = when (period) {
            GrowthPeriod.MONTH_3 -> now.minusMonths(3)
            GrowthPeriod.MONTH_6 -> now.minusMonths(6)
            GrowthPeriod.YEAR_1 -> now.minusYears(1)
        }
        return start to now
    }

    private fun GrowthRecord.toUI() = GrowthRecordUI(
        id = id,
        time = LocalDateTime.ofEpochSecond(time / 1000, 0, java.time.ZoneOffset.UTC)
            .format(dateFormatter),
        height = height,
        weight = weight,
        headCircumference = headCircumference
    )

    private fun GrowthRecordUI.toEntity(babyId: Long) = GrowthRecord(
        id = id,
        babyId = babyId,
        time = LocalDateTime.parse(time, dateFormatter)
            .toEpochSecond(java.time.ZoneOffset.UTC) * 1000,
        height = height,
        weight = weight,
        headCircumference = headCircumference
    )

    private fun GrowthPeriod.toPeriodMonths() = when (this) {
        GrowthPeriod.MONTH_3 -> 3
        GrowthPeriod.MONTH_6 -> 6
        GrowthPeriod.YEAR_1 -> 12
    }
} 