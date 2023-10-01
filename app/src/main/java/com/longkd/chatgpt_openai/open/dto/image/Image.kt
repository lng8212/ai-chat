package com.longkd.chatgpt_openai.open.dto.image

import com.google.gson.annotations.SerializedName

/**
 * An object containing either a URL or a base 64 encoded image.
 *
 * https://beta.openai.com/docs/api-reference/images
 */
class Image {
    /**
     * The URL where the image can be accessed.
     */
    var url: String? = null

    /**
     * Base64 encoded image string.
     */
    @SerializedName("b64_json")
    var b64Json: String? = null
}