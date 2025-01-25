package com.example.babycare.ui.sleep

import com.example.babycare.data.entity.SleepRecord
import com.example.babycare.data.repository.SleepRecordRepository
import com.example.babycare.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 睡眠记录视图状态
 */
data class SleepViewState(
    val currentSleep: SleepRecord? = null,
    val isSleeping: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 睡眠记录视图事件
 */
sealed class SleepEvent {
    data class StartSleep(val babyId: Long) : SleepEvent()
    data class StopSleep(val quality: Int? = null) : SleepEvent()
    data class UpdateSleep(val record: SleepRecord) : SleepEvent()
    data class DeleteSleep(val record: SleepRecord) : SleepEvent()
}

/**
 * 睡眠记录ViewModel
 */
@HiltViewModel
class SleepViewModel @Inject constructor(
    private val sleepRepository: SleepRecordRepository
) : BaseViewModel<SleepViewState, SleepEvent>() {

    override fun initViewState() = SleepViewState()

    fun getSleepRecords(babyId: Long): Flow<List<SleepRecord>> =
        sleepRepository.getSleepRecordsByBabyId(babyId)

    fun getSleepRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<SleepRecord>> =
        sleepRepository.getSleepRecordsByDateRange(babyId, startDate, endDate)

    override fun handleEvent(event: SleepEvent) {
        when (event) {
            is SleepEvent.StartSleep -> startSleep(event.babyId)
            is SleepEvent.StopSleep -> stopSleep(event.quality)
            is SleepEvent.UpdateSleep -> updateSleep(event.record)
            is SleepEvent.DeleteSleep -> deleteSleep(event.record)
        }
    }

    private fun startSleep(babyId: Long) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            val record = SleepRecord(
                babyId = babyId,
                startTime = System.currentTimeMillis()
            )
            val id = sleepRepository.insert(record)
            updateState { 
                it.copy(
                    isLoading = false,
                    isSleeping = true,
                    currentSleep = record.copy(id = id)
                )
            }
        }
    }

    private fun stopSleep(quality: Int?) {
        val currentSleep = viewState.value.currentSleep ?: return
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            val updatedRecord = currentSleep.copy(
                endTime = System.currentTimeMillis(),
                quality = quality
            )
            sleepRepository.update(updatedRecord)
            updateState { 
                it.copy(
                    isLoading = false,
                    isSleeping = false,
                    currentSleep = null
                )
            }
        }
    }

    private fun updateSleep(record: SleepRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            sleepRepository.update(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun deleteSleep(record: SleepRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            sleepRepository.delete(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    override fun handleError(error: Throwable) {
        updateState { it.copy(error = error.message, isLoading = false) }
    }
} 