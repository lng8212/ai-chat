/*
 * Created by longkd on 10/3/23, 10:27 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/3/23, 10:27 PM
 */

package com.longkd.chatgpt_openai.open

import retrofit2.Response

/**
 * @Author: longkd
 * @Since: 22:27 - 03/10/2023
 */
inline fun <reified T : Any> handleResponse(
    response: Response<T>,
): State<T> {
    return if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            State.Success(body)
        } else {
            State.Error("Empty body")
        }
    } else {
        State.Error(response.errorBody()?.string() ?: "Error")
    }
}