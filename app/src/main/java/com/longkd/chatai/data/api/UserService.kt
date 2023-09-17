package com.longkd.chatai.data.api

import com.longkd.base_android.annotation.ApiService
import com.longkd.base_android.annotation.Level
import com.longkd.base_android.annotation.LoggingLever
import com.longkd.chatai.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Author: longkd
 * @Since: 11:37 - 13/08/2023
 */

@ApiService(baseUrl = BuildConfig.BASE_URL)
@LoggingLever(level = Level.BODY)
interface UserService {
    @GET("api/users")
    suspend fun getAllData(@Query("page") page: Int): Response<com.longkd.chatai.data.api.response.UserResponse>
}