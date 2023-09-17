package com.longkd.base_android.base

import com.longkd.base_android.core.DataState
import com.longkd.base_android.core.UiState
import com.longkd.base_android.core.event.SingleEvent
import com.longkd.base_android.ktx.bindUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: longkd
 * @Since: 21:41 - 13/08/2023
 */
abstract class BaseGetDataViewModel<Directions> : BaseViewModel<Directions>() {
    protected fun bindUiState(
        state: MutableStateFlow<SingleEvent<UiState>> = _uiState,
        handler: suspend () -> DataState<*, *>
    ) = launch {
        state.emit(SingleEvent(UiState.LOADING))
        val result = try {
            handler.invoke()
        } catch (e: Exception) {
            DataState.Error(e)
        }
        if (result.isError) error = SingleEvent(result.error())
        result.bindUiState(state)
    }
}