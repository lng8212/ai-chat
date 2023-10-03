/*
 * Created by longkd on 10/3/23, 10:19 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/3/23, 10:19 PM
 */

package com.longkd.chatgpt_openai.open

import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Result
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @Author: longkd
 * @Since: 22:19 - 03/10/2023
 */
interface ChatRepository {
    suspend fun createCompletionV1Chat(request: Completion35Request): State<Completion35Result>
    suspend fun createCompletionV1ChatGPT4(request: Completion35Request): State<Completion35Result>
}