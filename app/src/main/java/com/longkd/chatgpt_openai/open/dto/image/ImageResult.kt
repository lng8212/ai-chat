package com.longkd.chatgpt_openai.open.dto.image

/**
 * An object with a list of image results.
 *
 * https://beta.openai.com/docs/api-reference/images
 */
class ImageResult {
    /**
     * The creation time in epoch seconds.
     */
    var createdAt: Long? = null

    /**
     * List of image results.
     */
    var data: List<Image>? = null
}