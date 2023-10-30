package com.longkd.chatgpt_openai.open.weather.model

import com.google.gson.annotations.SerializedName

data class Maximum(
    @SerializedName("unit")
    val unit: String, // F
    @SerializedName("unitType")
    val unitType: Int, // 18
    @SerializedName("value")
    val value: Double // 72.0
)