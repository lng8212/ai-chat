package com.longkd.base_android.core.event

/**
 * @Author: longkd
 * @Since: 22:33 - 11/08/2023
 */


/**
* Single event to create event only and only handler once
*
* When use [LiveData] to observer or [StateFlow] to collect a data [T] and you want handle that data **once** latest
*
*
*/
data class SingleEvent<T>(private val _value: T) {

    private var isHandled = false

    fun getValueIfNotHandled(): T? {
        if (isHandled) return null
        isHandled = true
        return _value
    }

    val value: T?
        get() = getValueIfNotHandled()

    fun requiredValue() = _value

    fun isHandled() = isHandled

    fun reset() {
        isHandled = false
    }
}