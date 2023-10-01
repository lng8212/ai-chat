package com.longkd.chatgpt_openai.base.model

import com.google.gson.annotations.SerializedName

data class TopicResponse(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("topicText") var topicText: String?,
    @SerializedName("topicContent") val topicContent: String?,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("suggestList") var suggestList: List<String>? = null
)