package com.longkd.chatgpt_openai.open.dto.image

import com.google.gson.annotations.SerializedName

/**
 * A request for OpenAi to create an image based on a prompt
 * All fields except prompt are optional
 *
 * https://beta.openai.com/docs/api-reference/images/create
 */
data class CreateImageRequest(
    /**
     * A text description of the desired image(s). The maximum length in 1000 characters.
     */
    @SerializedName("prompt")
    var prompt: String = "",

    /**
     * The number of images to generate. Must be between 1 and 10. Defaults to 1.
     */
    @SerializedName("n")
    var n: Int? = null,

    /**
     * The size of the generated images. Must be one of "256x256", "512x512", or "1024x1024". Defaults to "1024x1024".
     */
    @SerializedName("size")
    var size: String? = null,

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json. Defaults to url.
     */
    @SerializedName("response_format")
    var responseFormat: String? = null,

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    @SerializedName("user")
    var user: String? = null
)