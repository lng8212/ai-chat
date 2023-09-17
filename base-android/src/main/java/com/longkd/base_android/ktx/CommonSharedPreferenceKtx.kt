package com.longkd.base_android.ktx

import android.content.SharedPreferences
import com.google.gson.Gson
import com.longkd.base_android.data.CommonSharedPreference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
/**
 * @Author: longkd
 * @Since: 10:22 - 12/08/2023
 */
inline fun <reified T : Any> CommonSharedPreference.get(key: String, defValue: T? = null): T? {
    return when (T::class) {
        String::class -> getString(key, defValue as? String) as T?
        Int::class -> getInt(key, defValue as? Int ?: -1) as T
        Float::class -> getFloat(key, defValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defValue as? Long ?: -1L) as T
        Boolean::class -> getBoolean(key, defValue as? Boolean ?: false) as T
        else -> {
            val value = getString(key, null)
            if (value.isNullOrEmpty()) {
                defValue
            } else {
                Gson().fromJson(value, T::class.java)
            }
        }
    }
}

inline fun <reified T : Any> CommonSharedPreference.registerValueChangeListener(
    key: String,
    crossinline onChange: (T) -> Unit
): SharedPreferences.OnSharedPreferenceChangeListener {
    if (!contains(key)) throw Exception("SharedPreference don't contain key $key")
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _key ->
        if (_key == key) {
            val value: T = when (T::class) {
                String::class -> getString(key, "") as T
                Long::class -> getLong(key, -1) as T
                Int::class -> getInt(key, -1) as T
                Boolean::class -> getBoolean(key, false) as T
                Float::class -> getFloat(key, -1f) as T
                else -> {
                    Gson().fromJson(key, T::class.java)
                }
            }
            onChange(value)
        }
    }
    registerOnSharedPreferenceChangeListener(listener)
    return listener
}

fun CommonSharedPreference.unregisterValueChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    unregisterOnSharedPreferenceChangeListener(listener)
}

private inline fun <T> CommonSharedPreference.delegate(
    defaultValue: T,
    key: String?,
    crossinline getter: CommonSharedPreference.(String, T) -> T,
    crossinline setter: CommonSharedPreference.(String, T) -> CommonSharedPreference
): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>) =
            getter(key ?: property.name, defaultValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            setter(key ?: property.name, value)
        }
    }
}

fun CommonSharedPreference.int(defValue: Int = -1, key: String? = null) =
    delegate(defValue, key, CommonSharedPreference::getInt, CommonSharedPreference::putInt)

fun CommonSharedPreference.float(defValue: Float = -1f, key: String? = null) =
    delegate(defValue, key, CommonSharedPreference::getFloat, CommonSharedPreference::putFloat)

fun CommonSharedPreference.long(defValue: Long = -1L, key: String? = null) =
    delegate(defValue, key, CommonSharedPreference::getLong, CommonSharedPreference::putLong)

fun CommonSharedPreference.boolean(defValue: Boolean = false, key: String? = null) =
    delegate(defValue, key, CommonSharedPreference::getBoolean, CommonSharedPreference::putBoolean)

fun CommonSharedPreference.string(defValue: String = "", key: String? = null) =
    delegate(defValue, key, CommonSharedPreference::getString, CommonSharedPreference::putString)
