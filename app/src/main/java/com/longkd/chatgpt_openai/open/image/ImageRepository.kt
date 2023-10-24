/*
 * Created by longkd on 10/23/23, 9:41 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/23/23, 9:41 PM
 */

package com.longkd.chatgpt_openai.open.image

import com.longkd.chatgpt_openai.open.State
import com.longkd.chatgpt_openai.open.dto.image.CreateImageRequest
import com.longkd.chatgpt_openai.open.dto.image.ImageResult

/**
 * @Author: longkd
 * @Since: 21:41 - 23/10/2023
 */
interface ImageRepository {
    suspend fun generateImage(request: CreateImageRequest?): State<ImageResult>
}