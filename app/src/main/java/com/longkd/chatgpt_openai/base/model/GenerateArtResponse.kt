package com.longkd.chatgpt_openai.base.model

import com.google.gson.annotations.SerializedName

data class GenerateArtResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ByteArray
)
