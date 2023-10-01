
package com.longkd.chatgpt_openai.open.dto.completion

import com.longkd.chatgpt_openai.open.dto.Usage
import com.google.gson.annotations.SerializedName

/**
 * An object containing a response from the completion api
 *
 * https://beta.openai.com/docs/api-reference/completions/create
 */
class CompletionResult {
    /**
     * A unique id assigned to this completion.
     */
    @SerializedName("id")
    var id: String? = null

    /**
     * The type of object returned, should be "text_completion"
     */
    @SerializedName("text_completion")
    var textCompletion: String? = null

    /**
     * The creation time in epoch seconds.
     */
    @SerializedName("created")
    var created: Long = 0

    /**
     * The GPT-3 model used.
     */
    @SerializedName("model")
    var model: String? = null

    /**
     * A list of generated completions.
     */
    @SerializedName("choices")
    var choices: List<CompletionChoice>? = null

    /**
     * The API usage for this request
     */
    @SerializedName("usage")
    var usage: Usage? = null

    @SerializedName("error_code")
    var errorCode:Int = 0
}