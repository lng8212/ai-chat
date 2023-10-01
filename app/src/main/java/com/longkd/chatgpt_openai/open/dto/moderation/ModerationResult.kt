package com.longkd.chatgpt_openai.open.dto.moderation

/**
 * An object containing a response from the moderation api
 *
 * https://beta.openai.com/docs/api-reference/moderations/create
 */
class ModerationResult {
    /**
     * A unique id assigned to this moderation.
     */
    var id: String? = null

    /**
     * The GPT-3 model used.
     */
    var model: String? = null

    /**
     * A list of moderation scores.
     */
    var results: List<Moderation>? = null
}