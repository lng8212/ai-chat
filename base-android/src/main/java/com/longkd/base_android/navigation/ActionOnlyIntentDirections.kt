package com.longkd.base_android.navigation

import android.content.Intent
import kotlin.reflect.KClass

/**
 * @Author: longkd
 * @Since: 10:19 - 12/08/2023
 */
internal data class ActionOnlyIntentDirections(override val destination: KClass<*>) :
    IntentDirections {
    override val intent: Intent = Intent()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ActionOnlyIntentDirections
        return destination == that.destination
    }

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + destination.hashCode()
        return result
    }

    override fun toString(): String {
        return "ActionOnlyIntentDirections(destination=$destination)"
    }
}
