package com.longkd.base_android.base

import androidx.lifecycle.ViewModel
import com.longkd.base_android.core.DataState
import com.longkd.base_android.core.UiState
import com.longkd.base_android.core.event.SingleEvent
import com.longkd.base_android.ktx.bindUiState
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import com.longkd.base_android.navigation.Navigator

/**
 * @Author: longkd
 * @Since: 22:27 - 11/08/2023
 */
abstract class BaseViewModel<Directions> : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + SupervisorJob()

    override fun onCleared() {
        super.onCleared()
        cancel("onCleared ${this::class.simpleName} -> cancel coroutine ${coroutineContext[CoroutineName]}")
    }

    protected lateinit var navigator: Navigator<Directions>

    private lateinit var _viewController: WeakReference<BaseViewController>

    protected val viewController: BaseViewController?
        get() = _viewController.get()

    protected val _uiState = MutableStateFlow(SingleEvent(UiState.IDLE))
    val uiState: StateFlow<SingleEvent<UiState>> = _uiState

    var error: SingleEvent<Exception?> = SingleEvent(null)
        protected set

    val errorMessage: String?
        get() = error.getValueIfNotHandled()?.message

    fun setupNavigator(navigator: Navigator<Directions>) {
        this.navigator = navigator
    }

    fun setState(state: UiState) {
        _uiState.value = SingleEvent(state)
    }

    fun setViewController(viewController: BaseViewController) {
        this._viewController = WeakReference(viewController)
    }

    suspend fun emitState(state: UiState) {
        _uiState.emit(SingleEvent(state))
    }

    fun handle(handler: () -> DataState<*, *>) = launch {
        emitState(UiState.LOADING)
        val result = handler.invoke()
        if (result.isError) error = SingleEvent(result.error())
        result.bindUiState(_uiState)
    }

    fun navigator() = navigator

    fun <T : BaseViewController> viewController(): T = _viewController.get() as T

}