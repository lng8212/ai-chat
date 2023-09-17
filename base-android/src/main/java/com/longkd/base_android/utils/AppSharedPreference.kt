package com.longkd.base_android.utils

import android.content.Context
import com.longkd.base_android.data.CommonSharedPreference
import com.longkd.base_android.data.CommonSharedPreferenceFactory

/**
 * @Author: longkd
 * @Since: 10:32 - 12/08/2023
 */
object AppSharedPreference {
    private var appPref: CommonSharedPreference? = null

    private fun getApplicationName(applicationContext: Context): String {
        val applicationInfo = applicationContext.applicationInfo
        val nameId = applicationInfo.labelRes
        val appName = if (nameId == 0) {
            applicationInfo.nonLocalizedLabel.toString()
        } else {
            applicationContext.getString(nameId)
        }
        return "[Application] $appName"
    }

    fun create(applicationContext: Context, namePref: String? = null) {
        val name = namePref ?: getApplicationName(applicationContext)
        appPref = CommonSharedPreferenceFactory.create(applicationContext, name)
    }

    fun get(): CommonSharedPreference {
        return checkNotNull(appPref) {
            "Must call AppSharedPreference.create(context) first"
        }
    }

    val I: CommonSharedPreference
        get() = get()
}