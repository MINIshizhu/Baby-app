package com.example.babycare.ui.records

import androidx.lifecycle.viewModelScope
import com.example.babycare.data.entity.*
import com.example.babycare.data.repository.*
import com.example.babycare.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * 记录页面视图状态
 */
data class RecordsViewState(
    val selectedBabyId: Long? = null,
    val feedingRecords: List<FeedingRecordUI> = emptyList(),
    val sleepRecords: List<SleepRecordUI> = emptyList(),
    val diaperRecords: List<DiaperRecordUI> = emptyList(),
    val medicineRecords: List<MedicineRecordUI> = emptyList(),
    val waterRecords: List<WaterRecordUI> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 记录页面事件
 */
sealed class RecordsEvent {
    data class SelectBaby(val babyId: Long) : RecordsEvent()
    data class DeleteRecord(
        val recordType: RecordType,
        val recordId: Long
    ) : RecordsEvent()
}

enum class RecordType {
    FEEDING, SLEEP, DIAPER, MEDICINE, WATER
}

@HiltViewModel
class RecordsViewModel @Inject constructor(
    private val feedingRepository: FeedingRecordRepository,
    private val sleepRepository: SleepRecordRepository,
    private val diaperRepository: DiaperRecordRepository,
    private val medicineRepository: MedicineRecordRepository,
    private val waterRepository: WaterRecordRepository
) : BaseViewModel<RecordsViewState, RecordsEvent>() {

    override fun initViewState() = RecordsViewState()

    private val dateFormatter = DateTimeFormatter.ofPattern("HH:mm")

    init {
        // 假设有一个默认的婴儿ID
        handleEvent(RecordsEvent.SelectBaby(1L))
    }

    override fun handleEvent(event: RecordsEvent) {
        when (event) {
            is RecordsEvent.SelectBaby -> loadRecords(event.babyId)
            is RecordsEvent.DeleteRecord -> deleteRecord(event.recordType, event.recordId)
        }
    }

    private fun loadRecords(babyId: Long) {
        viewModelScope.launch {
            updateState { it.copy(selectedBabyId = babyId, isLoading = true) }

            // 使用combine合并所有数据流
            combine(
                feedingRepository.getFeedingRecordsByBabyId(babyId),
                sleepRepository.getSleepRecordsByBabyId(babyId),
                diaperRepository.getDiaperRecordsByBabyId(babyId),
                medicineRepository.getMedicineRecordsByBabyId(babyId),
                waterRepository.getWaterRecordsByBabyId(babyId)
            ) { feeding, sleep, diaper, medicine, water ->
                updateState { state ->
                    state.copy(
                        feedingRecords = feeding.map { it.toUI() },
                        sleepRecords = sleep.map { it.toUI() },
                        diaperRecords = diaper.map { it.toUI() },
                        medicineRecords = medicine.map { it.toUI() },
                        waterRecords = water.map { it.toUI() },
                        isLoading = false
                    )
                }
            }.catch { error ->
                updateState { it.copy(error = error.message, isLoading = false) }
            }.collect()
        }
    }

    private fun deleteRecord(type: RecordType, id: Long) {
        viewModelScope.launch {
            try {
                when (type) {
                    RecordType.FEEDING -> feedingRepository.delete(FeedingRecord(id = id, babyId = viewState.value.selectedBabyId ?: return@launch, startTime = 0))
                    RecordType.SLEEP -> sleepRepository.delete(SleepRecord(id = id, babyId = viewState.value.selectedBabyId ?: return@launch, startTime = 0))
                    RecordType.DIAPER -> diaperRepository.delete(DiaperRecord(id = id, babyId = viewState.value.selectedBabyId ?: return@launch, time = 0, type = 0))
                    RecordType.MEDICINE -> medicineRepository.delete(MedicineRecord(id = id, babyId = viewState.value.selectedBabyId ?: return@launch, time = 0, medicineName = "", dosage = 0f, unit = ""))
                    RecordType.WATER -> waterRepository.delete(WaterRecord(id = id, babyId = viewState.value.selectedBabyId ?: return@launch, time = 0, amount = 0))
                }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    // 实体转UI模型的扩展函数
    private fun FeedingRecord.toUI() = FeedingRecordUI(
        id = id,
        time = LocalDateTime.ofEpochSecond(startTime, 0, java.time.ZoneOffset.UTC).format(dateFormatter),
        isBottleFeeding = feedingType == 1,
        amount = amount,
        duration = if (endTime != null) ((endTime - startTime) / 60000).toInt() else null
    )

    // 其他转换函数...
}

// UI数据模型
data class FeedingRecordUI(
    val id: Long,
    val time: String,
    val isBottleFeeding: Boolean,
    val amount: Int? = null,
    val duration: Int? = null
)

// 其他UI数据模型... 