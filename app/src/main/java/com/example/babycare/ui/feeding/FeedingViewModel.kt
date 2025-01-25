package com.example.babycare.ui.feeding

import com.example.babycare.data.entity.FeedingRecord
import com.example.babycare.data.repository.FeedingRecordRepository
import com.example.babycare.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 喂奶记录视图状态
 */
data class FeedingViewState(
    val currentFeeding: FeedingRecord? = null,
    val isFeeding: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 喂奶记录视图事件
 */
sealed class FeedingEvent {
    data class StartFeeding(
        val babyId: Long,
        val feedingType: Int,
        val amount: Int? = null
    ) : FeedingEvent()
    
    object StopFeeding : FeedingEvent()
    data class UpdateFeeding(val record: FeedingRecord) : FeedingEvent()
    data class DeleteFeeding(val record: FeedingRecord) : FeedingEvent()
}

/**
 * 喂奶记录ViewModel
 */
@HiltViewModel
class FeedingViewModel @Inject constructor(
    private val feedingRepository: FeedingRecordRepository
) : BaseViewModel<FeedingViewState, FeedingEvent>() {

    override fun initViewState() = FeedingViewState()

    fun getFeedingRecords(babyId: Long): Flow<List<FeedingRecord>> =
        feedingRepository.getFeedingRecordsByBabyId(babyId)

    fun getFeedingRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<FeedingRecord>> =
        feedingRepository.getFeedingRecordsByDateRange(babyId, startDate, endDate)

    override fun handleEvent(event: FeedingEvent) {
        when (event) {
            is FeedingEvent.StartFeeding -> startFeeding(event.babyId, event.feedingType, event.amount)
            is FeedingEvent.StopFeeding -> stopFeeding()
            is FeedingEvent.UpdateFeeding -> updateFeeding(event.record)
            is FeedingEvent.DeleteFeeding -> deleteFeeding(event.record)
        }
    }

    private fun startFeeding(babyId: Long, feedingType: Int, amount: Int?) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            val record = FeedingRecord(
                babyId = babyId,
                startTime = System.currentTimeMillis(),
                feedingType = feedingType,
                amount = amount
            )
            val id = feedingRepository.insert(record)
            updateState { 
                it.copy(
                    isLoading = false,
                    isFeeding = true,
                    currentFeeding = record.copy(id = id)
                )
            }
        }
    }

    private fun stopFeeding() {
        val currentFeeding = viewState.value.currentFeeding ?: return
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            val updatedRecord = currentFeeding.copy(
                endTime = System.currentTimeMillis()
            )
            feedingRepository.update(updatedRecord)
            updateState { 
                it.copy(
                    isLoading = false,
                    isFeeding = false,
                    currentFeeding = null
                )
            }
        }
    }

    private fun updateFeeding(record: FeedingRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            feedingRepository.update(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun deleteFeeding(record: FeedingRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            feedingRepository.delete(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    override fun handleError(error: Throwable) {
        updateState { it.copy(error = error.message, isLoading = false) }
    }
} 