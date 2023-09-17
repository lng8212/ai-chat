package com.longkd.base_android.data

import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * @Author: longkd
 * @Since: 10:34 - 12/08/2023
 */
internal class CommonSharedPreferenceImpl(
    private val pref: SharedPreferences
) : CommonSharedPreference, SharedPreferences by pref {

    private val gson: Gson by lazy {
        Gson()
    }

    private fun edit(
        commit: Boolean = false,
        transform: SharedPreferences.Editor.() -> Unit
    ): CommonSharedPreference {
        val editor = edit()
        transform(editor)
        if (commit) editor.commit() else editor.apply()
        return this
    }

    override fun put(key: String, value: Any): CommonSharedPreference {
        when (value) {
            is Boolean -> putBoolean(key, value)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> {
                val valueString = gson.toJson(value)
                putString(key, valueString)
            }
        }
        return this
    }

    override fun putString(key: String, value: String?): CommonSharedPreference = edit { putString(key, value) }

    override fun putInt(key: String, value: Int): CommonSharedPreference = edit { putInt(key, value) }

    override fun putLong(key: String, value: Long): CommonSharedPreference = edit { putLong(key, value) }

    override fun putFloat(key: String, value: Float): CommonSharedPreference = edit { putFloat(key, value) }

    override fun putBoolean(key: String, value: Boolean): CommonSharedPreference = edit { putBoolean(key, value) }

    override fun putStringSet(key: String, value: Set<String>): CommonSharedPreference = edit { putStringSet(key, value) }

    override fun remove(key: String): CommonSharedPreference = edit { remove(key) }

    override fun clear(): CommonSharedPreference = edit { clear() }

    override fun putObject(key: String, value: Any): CommonSharedPreference {
        val objectString = gson.toJson(value)
        return putString(key, objectString)
    }

    override fun <T> getObject(key: String, classValue: Class<T>, defValue: T?): T? {
        val valueString = getString(key, null)
        return gson.fromJson(valueString, classValue)
    }
}