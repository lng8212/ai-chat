package com.longkd.base_android.ktx

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * @Author: longkd
 * @Since: 10:16 - 12/08/2023
 */
val Context.applicationName: String
    get() = applicationInfo.loadLabel(packageManager).toString()

val Context.applicationIcon: Drawable
    get() = applicationInfo.loadIcon(packageManager)

val Context.applicationDescription: String
    get() = applicationInfo.loadDescription(packageManager).toString()

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()

fun Context.showToast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, id, duration).show()
