package com.longkd.base_android.data.exception

/**
 * @Author: longkd
 * @Since: 12:52 - 13/08/2023
 */
data class ApiException(
    val statusCode: Int,
    override val message: String,
    val data: Any? = null
) : Exception(message) {
    companion object
}