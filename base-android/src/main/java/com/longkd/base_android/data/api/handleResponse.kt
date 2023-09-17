package com.longkd.base_android.data.api

import com.google.gson.Gson
import com.longkd.base_android.core.DataState
import com.longkd.base_android.data.exception.ApiException
import retrofit2.Response

/**
 * @Author: longkd
 * @Since: 12:52 - 13/08/2023
 */
inline fun <reified T : Any> handleResponse(
    response: Response<T>,
    errorClass: Class<*>? = null
): DataState<T, ApiException> {
    return if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            DataState.Success(body)
        } else {
            DataState.Error(
                ApiException(
                    statusCode = response.code(),
                    message = "Empty body",
                    data = null
                )
            )
        }
    } else {
        val error = response.errorBody()?.string()?.let {
            try {
                Gson().fromJson(it, errorClass ?: T::class.java)
            } catch (e: Exception) {
                null
            }
        }
        DataState.Error(
            ApiException(
                statusCode = response.code(),
                message = response.message(),
                data = error
            )
        )
    }
}