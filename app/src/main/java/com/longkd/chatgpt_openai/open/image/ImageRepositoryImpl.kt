/*
 * Created by longkd on 10/23/23, 9:42 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/23/23, 9:42 PM
 */

package com.longkd.chatgpt_openai.open.image

import com.longkd.chatgpt_openai.open.State
import com.longkd.chatgpt_openai.open.dto.image.CreateImageRequest
import com.longkd.chatgpt_openai.open.dto.image.ImageResult
import com.longkd.chatgpt_openai.open.handleResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Author: longkd
 * @Since: 21:42 - 23/10/2023
 */
@Singleton
class ImageRepositoryImpl @Inject constructor(private val service: ImageService) : ImageRepository {
    override suspend fun generateImage(request: CreateImageRequest?): State<ImageResult> {
        return handleResponse(service.generateImage(request))
    }
}