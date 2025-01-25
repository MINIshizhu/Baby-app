package com.example.babycare.ui.medicine

import com.example.babycare.data.entity.MedicineRecord
import com.example.babycare.data.repository.MedicineRecordRepository
import com.example.babycare.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 喂药记录视图状态
 */
data class MedicineViewState(
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 喂药记录视图事件
 */
sealed class MedicineEvent {
    data class AddMedicine(
        val babyId: Long,
        val medicineName: String,
        val dosage: Float,
        val unit: String,
        val note: String? = null,
        val reminderTime: Long? = null
    ) : MedicineEvent()
    data class UpdateMedicine(val record: MedicineRecord) : MedicineEvent()
    data class DeleteMedicine(val record: MedicineRecord) : MedicineEvent()
}

/**
 * 喂药记录ViewModel
 */
@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val medicineRepository: MedicineRecordRepository
) : BaseViewModel<MedicineViewState, MedicineEvent>() {

    override fun initViewState() = MedicineViewState()

    fun getMedicineRecords(babyId: Long): Flow<List<MedicineRecord>> =
        medicineRepository.getMedicineRecordsByBabyId(babyId)

    fun getMedicineRecordsByDateRange(
        babyId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<MedicineRecord>> =
        medicineRepository.getMedicineRecordsByDateRange(babyId, startDate, endDate)

    fun getUpcomingReminders(): Flow<List<MedicineRecord>> =
        medicineRepository.getUpcomingReminders()

    override fun handleEvent(event: MedicineEvent) {
        when (event) {
            is MedicineEvent.AddMedicine -> addMedicine(
                event.babyId,
                event.medicineName,
                event.dosage,
                event.unit,
                event.note,
                event.reminderTime
            )
            is MedicineEvent.UpdateMedicine -> updateMedicine(event.record)
            is MedicineEvent.DeleteMedicine -> deleteMedicine(event.record)
        }
    }

    private fun addMedicine(
        babyId: Long,
        medicineName: String,
        dosage: Float,
        unit: String,
        note: String?,
        reminderTime: Long?
    ) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            val record = MedicineRecord(
                babyId = babyId,
                time = System.currentTimeMillis(),
                medicineName = medicineName,
                dosage = dosage,
                unit = unit,
                note = note,
                reminderTime = reminderTime
            )
            medicineRepository.insert(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun updateMedicine(record: MedicineRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            medicineRepository.update(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun deleteMedicine(record: MedicineRecord) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            medicineRepository.delete(record)
            updateState { it.copy(isLoading = false) }
        }
    }

    override fun handleError(error: Throwable) {
        updateState { it.copy(error = error.message, isLoading = false) }
    }
} 