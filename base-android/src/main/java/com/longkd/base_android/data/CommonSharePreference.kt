package com.longkd.base_android.data

import android.content.SharedPreferences

/**
 * @Author: longkd
 * @Since: 10:26 - 12/08/2023
 */
interface CommonSharedPreference : SharedPreferences {

    fun put(key: String, value: Any): CommonSharedPreference

    fun putString(key: String, value: String?): CommonSharedPreference

    fun putInt(key: String, value: Int): CommonSharedPreference

    fun putLong(key: String, value: Long): CommonSharedPreference

    fun putFloat(key: String, value: Float): CommonSharedPreference

    fun putBoolean(key: String, value: Boolean): CommonSharedPreference

    fun putStringSet(key: String, value: Set<String>): CommonSharedPreference

    fun remove(key: String): CommonSharedPreference

    fun clear(): CommonSharedPreference

    fun putObject(key: String, value: Any): CommonSharedPreference

    fun <T> getObject(key: String, classValue: Class<T>, defValue: T? = null): T?

}