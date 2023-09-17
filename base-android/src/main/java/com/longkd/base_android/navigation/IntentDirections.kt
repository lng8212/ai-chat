package com.longkd.base_android.navigation

import android.content.Intent
import kotlin.reflect.KClass

/**
 * @Author: longkd
 * @Since: 22:43 - 11/08/2023
 */

interface IntentDirections {
    val destination: KClass<*>
    val intent: Intent
}