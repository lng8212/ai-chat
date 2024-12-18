
package com.longkd.chatgpt_openai.open.dto

/**
 * The OpenAI resources used by a request
 */
class Usage {
    /**
     * The number of prompt tokens used.
     */
    var promptTokens: Long = 0

    /**
     * The number of completion tokens used.
     */
    var completionTokens: Long = 0

    /**
     * The number of total tokens used
     */
    var totalTokens: Long = 0
}