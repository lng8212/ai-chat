package com.longkd.base_android.ktx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.longkd.base_android.navigation.ActionOnlyIntentDirections
import com.longkd.base_android.navigation.IntentDirections
import kotlin.reflect.KClass

/**
 * @Author: longkd
 * @Since: 10:18 - 12/08/2023
 */
val <T : Any> T.TAG: String
    get() = this::class.java.canonicalName ?: this::class.java.simpleName

/**
 * Create action intent direction to [T]
 */
fun <T : AppCompatActivity> KClass<T>.createActionIntentDirections(): IntentDirections {
    return ActionOnlyIntentDirections(this)
}

/**
 * Create action intent direction to [T] with [intentBuilder]
 */
fun <T : AppCompatActivity> KClass<T>.createActionIntentDirections(
    intentBuilder: Intent.() -> Unit
): IntentDirections {
    val action = ActionOnlyIntentDirections(this)
    action.intent.apply { intentBuilder() }
    return action
}