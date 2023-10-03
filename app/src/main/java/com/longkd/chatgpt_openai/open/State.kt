/*
 * Created by longkd on 10/3/23, 10:22 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/3/23, 10:22 PM
 */

package com.longkd.chatgpt_openai.open

/**
 * @Author: longkd
 * @Since: 22:22 - 03/10/2023
 */
sealed class State<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : State<T>(data = data)
    class Error<T>(errorMessage: String?) : State<T>(message = errorMessage)
    class Loading<T> : State<T>()
}
