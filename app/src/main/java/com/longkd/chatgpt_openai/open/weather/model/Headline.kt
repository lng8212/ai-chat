package com.longkd.chatgpt_openai.open.weather.model

import com.google.gson.annotations.SerializedName

data class Headline(
    @SerializedName("category")
    val category: String, // rain
    @SerializedName("effectiveDate")
    val effectiveDate: String, // 2023-10-29T19:00:00+07:00
    @SerializedName("effectiveEpochDate")
    val effectiveEpochDate: Int, // 1698580800
    @SerializedName("endDate")
    val endDate: String, // 2023-10-30T07:00:00+07:00
    @SerializedName("endEpochDate")
    val endEpochDate: Int, // 1698624000
    @SerializedName("link")
    val link: String, // http://www.accuweather.com/en/vn/hanoi/353412/daily-weather-forecast/353412?lang=en-us
    @SerializedName("mobileLink")
    val mobileLink: String, // http://www.accuweather.com/en/vn/hanoi/353412/daily-weather-forecast/353412?lang=en-us
    @SerializedName("severity")
    val severity: Int, // 5
    @SerializedName("text")
    val text: String // Expect showers Sunday night
)