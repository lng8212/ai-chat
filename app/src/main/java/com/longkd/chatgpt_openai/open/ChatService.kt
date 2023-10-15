/*
 * Created by longkd on 10/3/23, 10:17 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/3/23, 10:17 PM
 */

package com.longkd.chatgpt_openai.open

import com.longkd.chatgpt_openai.base.model.SummaryFileResponse
import com.longkd.chatgpt_openai.base.model.TopicResponse
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Result
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @Author: longkd
 * @Since: 22:17 - 03/10/2023
 */
interface ChatService {
    @POST("/api/v1/chat")
    suspend fun createCompletionV1Chat(@Body request: Completion35Request?): Response<Completion35Result>

    @POST("/api/v1/chat_v4")
    suspend fun createCompletionV1ChatGPT4(@Body request: Completion35Request?): Response<Completion35Result>

    @POST("api/v1/topic_chat")
    suspend fun completeTopicChat(@Body request: Completion35Request?): Response<TopicResponse>

    @Multipart
    @POST("/api/v1/summarize_file")
    suspend fun uploadSummaryFile(@Part file: MultipartBody.Part?): Response<SummaryFileResponse>
}