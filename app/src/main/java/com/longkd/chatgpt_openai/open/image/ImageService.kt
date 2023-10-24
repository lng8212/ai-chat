package com.longkd.chatgpt_openai.open.image


import com.longkd.chatgpt_openai.open.dto.image.CreateImageRequest
import com.longkd.chatgpt_openai.open.dto.image.ImageResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @Author: longkd
 * @Since: 21:38 - 23/10/2023
 */
interface ImageService {
    @POST("/api/v1/images/generations")
    suspend fun generateImage(@Body request: CreateImageRequest?): Response<ImageResult>
}