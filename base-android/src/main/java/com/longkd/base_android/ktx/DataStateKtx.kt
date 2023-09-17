package com.longkd.base_android.ktx

import androidx.lifecycle.MutableLiveData
import com.longkd.base_android.core.DataState
import com.longkd.base_android.core.UiState
import com.longkd.base_android.core.event.SingleEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @Author: longkd
 * @Since: 22:40 - 11/08/2023
 */

fun DataState<*, *>.toUiState(): UiState {
    return when (this) {
        is DataState.Loading -> UiState.LOADING
        is DataState.Success -> UiState.SUCCESS
        is DataState.Error -> UiState.ERROR
        else -> {
            UiState.SUCCESS
        }
    }
}

@OptIn(ExperimentalContracts::class)
suspend inline fun <D> state(
    crossinline block: suspend CoroutineScope.() -> D
): DataState<D, Exception> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return try {
        DataState.Success(coroutineScope { block() })
    } catch (e: Exception) {
        DataState.Error(e)
    }
}

@OptIn(ExperimentalContracts::class)
suspend inline fun <D> stateOn(
    dispatcher: CoroutineDispatcher,
    crossinline block: suspend CoroutineScope.() -> D
): DataState<D, Exception> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return try {
        DataState.Success(withContext(dispatcher) { block() })
    } catch (e: Exception) {
        DataState.Error(e)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <D, E : Exception> DataState<D, E>.onState(
    success: (data: D) -> Unit,
    error: (error: E) -> Unit,
    noinline loading: (() -> Unit)? = null,
) {
    contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(error, InvocationKind.AT_MOST_ONCE)
    }
    when (this) {
        is DataState.Success -> success(data)
        is DataState.Error -> error(this.error)
        else -> loading?.invoke()
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <D, E : Exception> DataState<D, E>.onSuccess(block: (D) -> Unit): DataState<D, E> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    if (this is DataState.Success) block.invoke(data)
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <D, E : Exception> DataState<D, E>.onError(block: (E) -> Unit): DataState<D, E> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    if (this is DataState.Error) block.invoke(error)
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <D, E : Exception, R : Any> DataState<D, E>.map(transform: (D) -> R): DataState<R, E> {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is DataState.Success -> DataState.Success(transform(data))
        is DataState.Error -> DataState.Error(error)
        else -> DataState.Loading
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <R, D> DataState<D, Exception>.fold(
    onSuccess: (value: D) -> R,
    onFailure: (exception: Exception) -> R
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (val error = getErrorOrNull()) {
        null -> onSuccess(get())
        else -> onFailure(error)
    }
}

suspend fun <D, E : Exception> DataState<D, E>.bindUiState(
    uiState: MutableStateFlow<SingleEvent<UiState>>
): DataState<D, E> {
    uiState.emit(SingleEvent(toUiState()))
    return this
}

fun <D, E : Exception> DataState<D, E>.bindUiState(
    uiState: MutableLiveData<SingleEvent<UiState>>
): DataState<D, E> {
    uiState.postValue(SingleEvent(toUiState()))
    return this
}
