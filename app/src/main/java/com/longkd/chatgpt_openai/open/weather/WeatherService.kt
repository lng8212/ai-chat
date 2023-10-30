/*
 * Created by longkd on 10/30/23, 10:36 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/30/23, 10:36 PM
 */

package com.longkd.chatgpt_openai.open.weather

import com.longkd.chatgpt_openai.open.weather.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * @Author: longkd
 * @Since: 22:36 - 30/10/2023
 */
interface WeatherService {
    @GET("/api/getCurrentWeather")
    suspend fun getWeatherCurrent(): Response<WeatherResponse>
}