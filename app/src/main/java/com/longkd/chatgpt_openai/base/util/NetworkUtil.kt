@file:Suppress("DEPRECATION")
package com.longkd.chatgpt_openai.base.util

import android.content.Context
import android.net.ConnectivityManager
import java.io.IOException


/**
 * @Author: longkd
 * @Since: 17:52 - 30/09/2023
*/
object NetworkUtil {

    fun isInternetAvailable(context: Context): Boolean {
        return isConnected(context)
    }

    private fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return run {
            val activeNetwork = cm.activeNetworkInfo
            activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }
}