

package com.longkd.chatgpt_openai.open.dto.image_art

import com.google.gson.annotations.SerializedName


class ImageArtRequest{
    @SerializedName("model")
    var model: String? = null

    @SerializedName("message_id")
    var messageId: String? = null

    @SerializedName("prompt")
    var prompt: String? = null

    @SerializedName("image_position")
    var imagePosition: String? = null

}