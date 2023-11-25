/*
 * Created by longkd on 10/23/23, 9:37 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/17/23, 10:02 PM
 */

package com.longkd.chatgpt_openai.open.chat

import com.longkd.chatgpt_openai.base.model.SummaryFileResponse
import com.longkd.chatgpt_openai.base.model.TopicResponse
import com.longkd.chatgpt_openai.open.State
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Result
import retrofit2.http.Body

/**
 * @Author: longkd
 * @Since: 22:19 - 03/10/2023
 */
interface ChatRepository {
    suspend fun createCompletionV1Chat(request: Completion35Request): State<Completion35Result>
    suspend fun createCompletionV1ChatGPT4(request: Completion35Request): State<Completion35Result>

    suspend fun completeTopicChat(request: Completion35Request): State<TopicResponse>

    suspend fun uploadSummaryFile(path: String): State<SummaryFileResponse>

    suspend fun completeSummaryChat(@Body request: Completion35Request?): State<Completion35Result>

    suspend fun uploadSummaryText(@Body request: Completion35Request?): State<SummaryFileResponse>

}