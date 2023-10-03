/*
 * Created by longkd on 10/3/23, 10:21 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/3/23, 10:21 PM
 */

package com.longkd.chatgpt_openai.open

import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Author: longkd
 * @Since: 22:21 - 03/10/2023
 */

@Singleton
class ChatRepositoryImpl @Inject constructor(private val service: ChatService) : ChatRepository {
    override suspend fun createCompletionV1Chat(request: Completion35Request): State<Completion35Result> {
        return handleResponse(service.createCompletionV1Chat(request))
    }

    override suspend fun createCompletionV1ChatGPT4(request: Completion35Request): State<Completion35Result> {
        return handleResponse(service.createCompletionV1ChatGPT4(request))
    }
}