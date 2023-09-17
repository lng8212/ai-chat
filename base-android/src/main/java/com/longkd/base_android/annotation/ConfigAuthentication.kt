package com.longkd.base_android.annotation

/**
 * @Author: longkd
 * @Since: 11:29 - 13/08/2023
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigAuthentication(
    val token: String,
    val type: String = "Bearer",
    val headerName: String = "Authorization",
)
