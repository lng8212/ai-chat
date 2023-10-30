package com.longkd.chatgpt_openai.open.weather.model

import com.google.gson.annotations.SerializedName

data class Temperature(
    @SerializedName("maximum")
    val maximum: Maximum,
    @SerializedName("minimum")
    val minimum: Minimum
)