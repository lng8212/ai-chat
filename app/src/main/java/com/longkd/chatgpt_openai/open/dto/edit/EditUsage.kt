package com.longkd.chatgpt_openai.open.dto.edit

/**
 * An object containing the API usage for an edit request
 *
 * Deprecated, use [com.longkd.chatgpt_openai.open.dto.Usage] instead
 *
 * https://beta.openai.com/docs/api-reference/edits/create
 */
@Deprecated("")
class EditUsage {
    /**
     * The number of prompt tokens consumed.
     */
    var promptTokens: String? = null

    /**
     * The number of completion tokens consumed.
     */
    var completionTokens: String? = null

    /**
     * The number of total tokens consumed.
     */
    var totalTokens: String? = null
}