package com.longkd.chatgpt_openai.open.weather.model

import com.google.gson.annotations.SerializedName

data class DailyForecast(
    @SerializedName("date")
    val date: String, // 2023-10-29T07:00:00+07:00
    @SerializedName("day")
    val day: Day,
    @SerializedName("epochDate")
    val epochDate: Int, // 1698537600
    @SerializedName("link")
    val link: String, // http://www.accuweather.com/en/vn/hanoi/353412/daily-weather-forecast/353412?day=1&lang=en-us
    @SerializedName("mobileLink")
    val mobileLink: String, // http://www.accuweather.com/en/vn/hanoi/353412/daily-weather-forecast/353412?day=1&lang=en-us
    @SerializedName("night")
    val night: Night,
    @SerializedName("sources")
    val sources: List<String>,
    @SerializedName("temperature")
    val temperature: Temperature
)