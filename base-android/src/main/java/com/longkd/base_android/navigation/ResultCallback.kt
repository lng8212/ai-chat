package com.longkd.base_android.navigation

import android.os.Bundle

/**
 * @Author: longkd
 * @Since: 22:30 - 11/08/2023
 */
interface ResultCallback {
    fun onResult(key: String, data: Bundle?)
}