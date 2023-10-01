

package com.longkd.chatgpt_openai.open.dto.moderation

/**
 * A request for OpenAi to detect if text violates OpenAi's content policy.
 *
 * https://beta.openai.com/docs/api-reference/moderations/create
 */
class ModerationRequest {
    /**
     * The input text to classify.
     */
    var input: String = ""

    /**
     * The name of the model to use, defaults to text-moderation-stable.
     */
    var model: String? = null
}