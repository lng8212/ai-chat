package com.longkd.chatgpt_openai.open.weather.model

import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("hasPrecipitation")
    val hasPrecipitation: Boolean, // true
    @SerializedName("icon")
    val icon: Int, // 12
    @SerializedName("iconPhrase")
    val iconPhrase: String, // Showers
    @SerializedName("precipitationIntensity")
    val precipitationIntensity: String, // Light
    @SerializedName("precipitationType")
    val precipitationType: String // Rain
)