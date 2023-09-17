package com.longkd.base_android.core.async

import android.util.Log
import com.longkd.base_android.ktx.TAG
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * @Author: longkd
 * @Since: 10:50 - 12/08/2023
 */
suspend fun <T> withContextHandler(
    context: CoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> T
): T {
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(context.TAG, "withContextHandler: ", throwable)
        handleException?.invoke(coroutineContext, throwable)
    }
    return withContext(context + coroutineExceptionHandler, block)
}