package com.longkd.base_android.data

import android.content.Context

/**
 * @Author: longkd
 * @Since: 10:33 - 12/08/2023
 */
object CommonSharedPreferenceFactory {
    fun create(
        context: Context,
        namePref: String,
        modeOpen: Int = Context.MODE_PRIVATE
    ): CommonSharedPreference {
        val pref = context.getSharedPreferences(namePref, modeOpen)
        return CommonSharedPreferenceImpl(pref)
    }
}