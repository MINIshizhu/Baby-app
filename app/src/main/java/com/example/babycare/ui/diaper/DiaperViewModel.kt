package com.example.babycare.ui.diaper

import com.example.babycare.data.entity.DiaperRecord
import com.example.babycare.data.repository.DiaperRecordRepository
import com.example.babycare.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 排便记录视图状态
 */
data class DiaperViewState(
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 排便记录视图事件
 */
sealed class DiaperEvent {
    data class AddDiaper(
        val babyId: Long,
        val type: Int,
        val status: Int = 0,
        val note: String? = null
    ) : DiaperEvent()
    data class UpdateDiaper(val record: DiaperRecord) : DiaperEvent()
    data class DeleteDiaper(val record: DiaperRecord) : DiaperEvent()
}

/**
 * 排便记录ViewModel
 */
@HiltViewModel
class DiaperViewModel @Inject constructor(
    private val diaperRepository: DiaperRecordRepository
) : BaseViewModel<DiaperViewState, DiaperEvent>() {

    override fun initViewState() = DiaperViewState()

    fun getDiaperRecords(babyId: Long): Flow<List<DiaperRecord>> =
        diaperRepository.getDiaperRecordsByBabyId(babyId)

    fun getDiaperRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<DiaperRecord>> =
        diaperRepository.getDiaperRecordsByDateRange(babyId, startDate, endDate)

    override fun handleEvent(event: DiaperEvent) {
        when (event) {
            is DiaperEvent.AddDiaper -> addDiaper(event.babyId, event.type, event.status, event.note)
            is DiaperEvent.UpdateDiaper -> updateDiaper(event.record)
            is DiaperEvent.DeleteDiaper -> deleteDiaper(event.record)
        }
    }

    private fun addDiaper(babyId: Long, type: Int, status: Int, note: String?) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            val record = DiaperRecord(
                babyId = babyId,
                time = System.currentTimeMillis(),
                type = type,
                status = status,
                note = note
            )
            diaperRepository.insert(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun updateDiaper(record: DiaperRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            diaperRepository.update(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun deleteDiaper(record: DiaperRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            diaperRepository.delete(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    override fun handleError(error: Throwable) {
        updateState { it.copy(error = error.message, isLoading = false) }
    }
} 