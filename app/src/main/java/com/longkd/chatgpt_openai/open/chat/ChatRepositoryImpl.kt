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
import com.longkd.chatgpt_openai.open.handleResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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

    override suspend fun completeTopicChat(request: Completion35Request): State<TopicResponse> {
        return handleResponse(service.completeTopicChat(request))
    }

    override suspend fun uploadSummaryFile(path: String): State<SummaryFileResponse> {
        val mFile = java.io.File(path)
        val fileBody = mFile.asRequestBody("application/pdf".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", mFile.name, fileBody)
        return handleResponse(service.uploadSummaryFile(body))
    }

    override suspend fun completeSummaryChat(request: Completion35Request?): State<Completion35Result> {
        return handleResponse(service.completeSummaryChat(request))
    }

    override suspend fun uploadSummaryText(request: Completion35Request?): State<SummaryFileResponse> {
        return handleResponse(service.uploadSummaryText(request))
    }
}