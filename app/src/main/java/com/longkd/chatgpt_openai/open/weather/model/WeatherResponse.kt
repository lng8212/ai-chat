package com.longkd.chatgpt_openai.open.weather.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("dailyForecasts")
    val dailyForecasts: List<DailyForecast>,
    @SerializedName("deadline")
    val headline: Headline
)