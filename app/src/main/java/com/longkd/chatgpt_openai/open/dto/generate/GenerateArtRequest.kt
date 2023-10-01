

package com.longkd.chatgpt_openai.open.dto.generate

import com.google.gson.annotations.SerializedName


class GenerateArtRequest {
    @SerializedName("model")
    var model: String? = null

    @SerializedName("message_id")
    var messageId: String? = null

    @SerializedName("prompt")
    var prompt: String? = null

    @SerializedName("width")
    var width: Int? = null

    @SerializedName("height")
    var height: Int? = null
}


