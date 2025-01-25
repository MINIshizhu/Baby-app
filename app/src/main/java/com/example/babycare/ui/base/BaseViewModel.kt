package com.example.babycare.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel基类
 *
 * @param ViewState 视图状态类型
 * @param ViewEvent 视图事件类型
 */
abstract class BaseViewModel<ViewState, ViewEvent> : ViewModel() {
    
    private val _viewState = MutableStateFlow(initViewState())
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    /**
     * 初始化视图状态
     */
    abstract fun initViewState(): ViewState

    /**
     * 处理视图事件
     */
    abstract fun handleEvent(event: ViewEvent)

    /**
     * 更新视图状态
     */
    protected fun updateState(update: (ViewState) -> ViewState) {
        _viewState.value = update(_viewState.value)
    }

    /**
     * 启动协程
     */
    protected fun launchCoroutine(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    /**
     * 处理错误
     */
    protected open fun handleError(error: Throwable) {
        // 子类可以重写此方法来处理特定的错误
    }
} 