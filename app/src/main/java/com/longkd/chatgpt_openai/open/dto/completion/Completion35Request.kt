

package com.longkd.chatgpt_openai.open.dto.completion

import com.google.gson.annotations.SerializedName


class Completion35Request {
    @SerializedName("model")
    var model: String? = null
    @SerializedName("messages")
    var messages: ArrayList<Message35Request> = arrayListOf()
    @SerializedName("max_tokens")
    var maxTokens: Int? = null

    @SerializedName("stop")
    var stop: List<String>? = null

}