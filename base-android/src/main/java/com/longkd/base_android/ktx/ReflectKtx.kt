package com.longkd.base_android.ktx

import com.google.gson.annotations.SerializedName
import java.lang.reflect.Field

/**
 * @Author: longkd
 * @Since: 12:54 - 13/08/2023
 */
fun Field.serializedName(): String = getAnnotation(SerializedName::class.java)?.value ?: name