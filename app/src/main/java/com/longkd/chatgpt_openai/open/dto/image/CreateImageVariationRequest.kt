package com.longkd.chatgpt_openai.open.dto.image

/**
 * A request for OpenAi to create a variation of an image
 * All fields are optional
 *
 * https://beta.openai.com/docs/api-reference/images/create-variation
 */
class CreateImageVariationRequest {
    /**
     * The number of images to generate. Must be between 1 and 10. Defaults to 1.
     */
    var n: Int=1

    /**
     * The size of the generated images. Must be one of "256x256", "512x512", or "1024x1024". Defaults to "1024x1024".
     */
    var size: String="256x256"

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json. Defaults to url.
     */
    var responseFormat: String="b64_json"

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    var user: String="user"
}