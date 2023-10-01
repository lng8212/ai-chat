
package com.longkd.chatgpt_openai.open.dto.completion

import com.google.gson.annotations.SerializedName


data class Message35Request(
    @SerializedName("role")
    var role: String? = null,
    @SerializedName("content")
    var content: String? = null
)