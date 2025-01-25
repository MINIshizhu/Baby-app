package com.example.babycare.ui.water

import com.example.babycare.data.entity.WaterRecord
import com.example.babycare.data.repository.WaterRecordRepository
import com.example.babycare.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 喂水记录视图状态
 */
data class WaterViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val dailyTotal: Int = 0
)

/**
 * 喂水记录视图事件
 */
sealed class WaterEvent {
    data class AddWater(
        val babyId: Long,
        val amount: Int,
        val temperature: Int = 0,
        val note: String? = null
    ) : WaterEvent()
    data class UpdateWater(val record: WaterRecord) : WaterEvent()
    data class DeleteWater(val record: WaterRecord) : WaterEvent()
}

/**
 * 喂水记录ViewModel
 */
@HiltViewModel
class WaterViewModel @Inject constructor(
    private val waterRepository: WaterRecordRepository
) : BaseViewModel<WaterViewState, WaterEvent>() {

    override fun initViewState() = WaterViewState()

    fun getWaterRecords(babyId: Long): Flow<List<WaterRecord>> =
        waterRepository.getWaterRecordsByBabyId(babyId)

    fun getWaterRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<WaterRecord>> =
        waterRepository.getWaterRecordsByDateRange(babyId, startDate, endDate)

    fun getTotalWaterAmount(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<Int?> =
        waterRepository.getTotalWaterAmount(babyId, startDate, endDate)

    override fun handleEvent(event: WaterEvent) {
        when (event) {
            is WaterEvent.AddWater -> addWater(
                event.babyId,
                event.amount,
                event.temperature,
                event.note
            )
            is WaterEvent.UpdateWater -> updateWater(event.record)
            is WaterEvent.DeleteWater -> deleteWater(event.record)
        }
    }

    private fun addWater(
        babyId: Long,
        amount: Int,
        temperature: Int,
        note: String?
    ) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            val record = WaterRecord(
                babyId = babyId,
                time = System.currentTimeMillis(),
                amount = amount,
                temperature = temperature,
                note = note
            )
            waterRepository.insert(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun updateWater(record: WaterRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            waterRepository.update(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun deleteWater(record: WaterRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            waterRepository.delete(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    override fun handleError(error: Throwable) {
        updateState { it.copy(error = error.message, isLoading = false) }
    }
} 