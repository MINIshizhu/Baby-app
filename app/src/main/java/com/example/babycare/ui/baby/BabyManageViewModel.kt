package com.example.babycare.ui.baby

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.babycare.data.entity.Baby
import com.example.babycare.data.repository.BabyRepository
import com.example.babycare.ui.base.BaseViewModel
import com.example.babycare.utils.ImageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * 宝宝管理视图状态
 */
data class BabyManageViewState(
    val babies: List<Baby> = emptyList(),
    val currentBaby: Baby? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingBaby: Baby? = null
)

/**
 * 宝宝管理事件
 */
sealed class BabyManageEvent {
    object AddBaby : BabyManageEvent()
    data class EditBaby(val baby: Baby) : BabyManageEvent()
    data class DeleteBaby(val baby: Baby) : BabyManageEvent()
    data class SelectBaby(val baby: Baby) : BabyManageEvent()
    data class SaveBaby(
        val name: String,
        val gender: Int,
        val birthday: Long,
        val avatarUri: Uri?
    ) : BabyManageEvent()
    object DismissDialog : BabyManageEvent()
}

@HiltViewModel
class BabyManageViewModel @Inject constructor(
    private val repository: BabyRepository,
    private val imageManager: ImageManager
) : BaseViewModel<BabyManageViewState, BabyManageEvent>() {

    override fun initViewState() = BabyManageViewState()

    init {
        viewModelScope.launch {
            combine(
                repository.getAllBabies(),
                repository.getCurrentBaby()
            ) { babies, currentBaby ->
                updateState { state ->
                    state.copy(
                        babies = babies,
                        currentBaby = currentBaby,
                        isLoading = false
                    )
                }
            }.catch { error ->
                updateState { it.copy(error = error.message) }
            }.collect()
        }
    }

    override fun handleEvent(event: BabyManageEvent) {
        when (event) {
            is BabyManageEvent.AddBaby -> {
                updateState { it.copy(showAddDialog = true) }
            }
            is BabyManageEvent.EditBaby -> {
                updateState { 
                    it.copy(
                        showEditDialog = true,
                        editingBaby = event.baby
                    )
                }
            }
            is BabyManageEvent.DeleteBaby -> {
                deleteBaby(event.baby)
            }
            is BabyManageEvent.SelectBaby -> {
                selectBaby(event.baby)
            }
            is BabyManageEvent.SaveBaby -> {
                saveBaby(
                    event.name,
                    event.gender,
                    event.birthday,
                    event.avatarUri
                )
            }
            is BabyManageEvent.DismissDialog -> {
                updateState { 
                    it.copy(
                        showAddDialog = false,
                        showEditDialog = false,
                        editingBaby = null
                    )
                }
            }
        }
    }

    private fun saveBaby(
        name: String,
        gender: Int,
        birthday: Long,
        avatarUri: Uri?
    ) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                // 保存头像
                val avatarPath = avatarUri?.let { uri ->
                    imageManager.saveImage(uri)
                }

                val baby = viewState.value.editingBaby?.copy(
                    name = name,
                    gender = gender,
                    birthday = birthday,
                    avatar = avatarPath
                ) ?: Baby(
                    name = name,
                    gender = gender,
                    birthday = birthday,
                    avatar = avatarPath
                )

                if (baby.id == 0L) {
                    repository.insertBaby(baby)
                } else {
                    repository.updateBaby(baby)
                }

                updateState { 
                    it.copy(
                        showAddDialog = false,
                        showEditDialog = false,
                        editingBaby = null,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                updateState { 
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun deleteBaby(baby: Baby) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                repository.deleteBaby(baby)
                // 删除头像文件
                baby.avatar?.let { avatar ->
                    imageManager.deleteImage(avatar)
                }
                updateState { it.copy(isLoading = false) }
            } catch (e: Exception) {
                updateState { 
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun selectBaby(baby: Baby) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                repository.setCurrentBaby(baby.id)
                updateState { it.copy(isLoading = false) }
            } catch (e: Exception) {
                updateState { 
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }
} 