package com.longkd.base_android.ktx

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @Author: longkd
 * @Since: 10:17 - 12/08/2023
 */
fun <T> CoroutineScope.asyncHandler (
    context: CoroutineContext = EmptyCoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? =  null,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    lateinit var result: Deferred<T>
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        if (result.isActive) result.cancel()
        Log.e(coroutineContext[CoroutineName]?.name, "asyncHandler: ", throwable)
        handleException?.invoke(coroutineContext, throwable)
    }
    result = async(context + coroutineExceptionHandler, block = block)
    return result
}

/**
 * When throw an exception this coroutine must be cancel
 *
 */

fun CoroutineScope.launchHandler (
    context: CoroutineContext = EmptyCoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? =  null,
    block: suspend CoroutineScope.() -> Unit
): Job {
    lateinit var result: Job
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(coroutineContext[CoroutineName]?.name, "launchHandler: ", throwable)
        if (result.isActive) result.cancel()
        handleException?.invoke(coroutineContext, throwable)
    }
    result = launch(context + coroutineExceptionHandler, block = block)
    return result
}
