

package com.longkd.chatgpt_openai.open.dto

/**
 * A wrapper class to fit the OpenAI engine and search endpoints
 */
class OpenAiResponse<T> {
    /**
     * A list containing the actual results
     */
    var data: List<T>? = null

    /**
     * The type of object returned, should be "list"
     */
    var list: String? = null
}