package com.longkd.base_android.utils

import android.content.Context
import com.longkd.base_android.connectivity.ConnectivityObserver
import com.longkd.base_android.connectivity.NetworkConnectivityObserver
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect

/**
 * @Author: longkd
 * @Since: 10:43 - 12/08/2023
 */
object NetworkUtils {
    private var applicationContext: Context? = null

    var status: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE
        private set

    internal fun installIn(context: Context) {
        applicationContext = context
    }

    private fun getNetWorkConnectObserver(): ConnectivityObserver {
        val context = requireNotNull(applicationContext) { "Must call installIn(context) first" }
        return NetworkConnectivityObserver(context)
    }

    suspend fun observer(onChange: ((ConnectivityObserver.Status) -> Unit)? = null): Unit = getNetWorkConnectObserver().observer().onEach {
        status = it
        onChange?.invoke(status)
    }.collect()

    fun hasNetwork(): Boolean = status.hasNetwork()

    fun cleared() {
        applicationContext = null
    }
}