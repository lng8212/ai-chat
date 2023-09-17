package com.longkd.base_android.annotation

/**
 * @Author: longkd
 * @Since: 11:29 - 13/08/2023
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableAuthentication(
    /**
     * Key that store token in share preference
     */
    val key: String,
    val type: String = "Bearer",
    val headerName: String = "Authorization",
    /**
     * isAutoUpdate == true: auto update [token] from SharedPreference without getting for each request
     * isAutoUpdate == false: for each request get [token] from SharedPreference by [tokenKey]
     */
    val isAutoUpdate: Boolean = false
)
