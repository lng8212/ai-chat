package com.longkd.base_android.ktx

import android.util.Base64
import android.util.Patterns

/**
 * @Author: longkd
 * @Since: 10:21 - 12/08/2023
 */
fun String.decode(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(Charsets.UTF_8)
}

fun String.encode(): String {
    return Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

fun String.isValidEmail(): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(this).matches()
}

