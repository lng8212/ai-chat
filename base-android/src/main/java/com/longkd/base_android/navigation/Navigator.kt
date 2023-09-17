package com.longkd.base_android.navigation

import android.os.Bundle
import androidx.annotation.IdRes

/**
 * @Author: longkd
 * @Since: 22:30 - 11/08/2023
 */
interface Navigator<Directions> {
    fun navigateTo(directions: Directions)
    fun navigateOff(directions: Directions)
    fun navigateOffAll(directions: Directions)
    fun navigateForResult(directions: Directions, key: String, result: ResultCallback)
    fun back(key: String? = null, data: Bundle? = null)
    fun backTo(@IdRes id: Int, inclusive: Boolean, key: String? = null, data: Bundle? = null)
}
