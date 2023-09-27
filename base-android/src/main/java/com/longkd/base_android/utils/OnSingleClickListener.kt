package com.longkd.base_android.utils

import android.os.SystemClock
import android.view.View

/**
 * @Author: longkd
 * @Since: 00:00 - 28/09/2023
 */
abstract class OnSingleClickListener : View.OnClickListener {
    private var mLastClickTime: Long = 0

    abstract fun onSingleClick(v: View?)
    override fun onClick(v: View?) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime
        if (elapsedTime <= MIN_CLICK_INTERVAL) return
        onSingleClick(v)
    }

    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 400
    }
}