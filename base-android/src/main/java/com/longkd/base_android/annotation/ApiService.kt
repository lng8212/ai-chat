package com.longkd.base_android.annotation

/**
 * @Author: longkd
 * @Since: 11:28 - 13/08/2023
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiService(val baseUrl: String)