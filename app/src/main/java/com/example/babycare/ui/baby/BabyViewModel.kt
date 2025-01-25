package com.example.babycare.ui.baby

import com.example.babycare.data.entity.Baby
import com.example.babycare.data.repository.BabyRepository
import com.example.babycare.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * 婴儿管理视图状态
 */
data class BabyViewState(
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 婴儿管理视图事件
 */
sealed class BabyEvent {
    data class SelectBaby(val babyId: Long) : BabyEvent()
    data class AddBaby(
        val name: String,
        val birthDate: LocalDate,
        val gender: Int,
        val avatar: String?
    ) : BabyEvent()
    data class UpdateBaby(val baby: Baby) : BabyEvent()
    data class DeleteBaby(val baby: Baby) : BabyEvent()
}

/**
 * 婴儿管理ViewModel
 */
@HiltViewModel
class BabyViewModel @Inject constructor(
    private val babyRepository: BabyRepository
) : BaseViewModel<BabyViewState, BabyEvent>() {

    val babies: Flow<List<Baby>> = babyRepository.getAllBabies()

    override fun initViewState() = BabyViewState()

    override fun handleEvent(event: BabyEvent) {
        when (event) {
            is BabyEvent.SelectBaby -> selectBaby(event.babyId)
            is BabyEvent.AddBaby -> addBaby(event.name, event.birthDate, event.gender, event.avatar)
            is BabyEvent.UpdateBaby -> updateBaby(event.baby)
            is BabyEvent.DeleteBaby -> deleteBaby(event.baby)
        }
    }

    private fun selectBaby(babyId: Long) {
        launchCoroutine {
            val baby = babyRepository.getById(babyId)
            updateState { it.copy(selectedBaby = baby) }
        }
    }

    private fun addBaby(name: String, birthDate: LocalDate, gender: Int, avatar: String?) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            val baby = Baby(
                name = name,
                birthDate = birthDate,
                gender = gender,
                avatar = avatar
            )
            babyRepository.insert(baby)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun updateBaby(baby: Baby) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            babyRepository.update(baby)
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun deleteBaby(baby: Baby) {
        launchCoroutine {
            updateState { it.copy(isLoading = true) }
            babyRepository.delete(baby)
            updateState { 
                it.copy(
                    isLoading = false,
                    selectedBaby = if (it.selectedBaby?.id == baby.id) null else it.selectedBaby
                )
            }
        }
    }

    override fun handleError(error: Throwable) {
        updateState { it.copy(error = error.message, isLoading = false) }
    }
} 