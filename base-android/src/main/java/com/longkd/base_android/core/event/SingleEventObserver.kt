package com.longkd.base_android.core.event

import androidx.lifecycle.Observer

/**
 * @Author: longkd
 * @Since: 10:50 - 12/08/2023
 */
class SingleEventObserver <T> (private val onChanged: (T) -> Unit) : Observer<SingleEvent<T>> {

    override fun onChanged(value: SingleEvent<T>) {
        value.value?.let {
            onChanged.invoke(it)
        }
    }
}