package com.longkd.chatgpt_openai.open.dto.image

import com.google.gson.annotations.SerializedName

/**
 * An object with a list of image results.
 *
 * https://beta.openai.com/docs/api-reference/images
 */
class ImageResult {
    /**
     * The creation time in epoch seconds.
     */
    @SerializedName("created")
    var createdAt: Long? = null

    /**
     * List of image results.
     */
    @SerializedName("data")
    var data: List<Image>? = null
}