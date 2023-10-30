/*
 * Created by longkd on 10/30/23, 10:37 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/30/23, 10:37 PM
 */

package com.longkd.chatgpt_openai.open.weather

import com.longkd.chatgpt_openai.open.State
import com.longkd.chatgpt_openai.open.handleResponse
import com.longkd.chatgpt_openai.open.weather.model.WeatherResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Author: longkd
 * @Since: 22:37 - 30/10/2023
 */
@Singleton
class WeatherRepositoryImpl @Inject constructor(private val service: WeatherService) :
    WeatherRepository {
    override suspend fun getWeatherCurrent(): State<WeatherResponse> {
        return handleResponse(service.getWeatherCurrent())
    }

}