package com.longkd.base_android.core.async

import com.longkd.base_android.ktx.launchHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * @Author: longkd
 * @Since: 10:49 - 12/08/2023
 */
/**
 * create a new [CoroutineScope]
 * start it with async
 *
 * @param context [CoroutineContext] to create [CoroutineScope]
 * @param handleException handler when throw exception in [block]
 * @param block that run in [CoroutineScope]
 * @return [Deferred]
 */

fun launchHandler(
    context: CoroutineContext,
    handleException: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val coroutineScope = CoroutineScope(context + SupervisorJob())
    return coroutineScope.launchHandler(
        handleException = handleException,
        block = block
    )
}