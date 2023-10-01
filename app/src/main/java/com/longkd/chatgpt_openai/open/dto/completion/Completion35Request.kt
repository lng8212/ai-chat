

package com.longkd.chatgpt_openai.open.dto.completion

import com.google.gson.annotations.SerializedName


class Completion35Request {
    @SerializedName("model")
    var model: String? = null
    @SerializedName("messages")
    var messages: ArrayList<Message35Request> = arrayListOf()

    /**
     * The maximum number of tokens to generate.
     * Requests can use up to 2048 tokens shared between prompt and completion.
     * (One token is roughly 4 characters for normal English text)
     */
    @SerializedName("max_tokens")
    var maxTokens: Int? = null

    /**
     * What sampling temperature to use. Higher values means the model will take more risks.
     * Try 0.9 for more creative applications, and 0 (argmax sampling) for ones with a well-defined answer.
     *
     * We generally recommend using this or [CompletionRequest.topP] but not both.
     */
    @SerializedName("temperature")
    var temperature: Double? = null

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of
     * the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are
     * considered.
     *
     * We generally recommend using this or [CompletionRequest.temperature] but not both.
     */
    @SerializedName("top_p")
    var topP: Double? = null

    /**
     * How many completions to generate for each prompt.
     *
     * Because this parameter generates many completions, it can quickly consume your token quota.
     * Use carefully and ensure that you have reasonable settings for [CompletionRequest.maxTokens] and [CompletionRequest.stop].
     */
    @SerializedName("n")
    var n: Int? = null

    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     * The returned text will not contain the stop sequence.
     */
    @SerializedName("stop")
    var stop: List<String>? = null

    /**
     * Number between 0 and 1 (default 0) that penalizes new tokens based on whether they appear in the text so far.
     * Increases the model's likelihood to talk about new topics.
     */
    @SerializedName("presence_penalty")
    var presencePenalty: Double? = null

    /**
     * Number between 0 and 1 (default 0) that penalizes new tokens based on their existing frequency in the text so far.
     * Decreases the model's likelihood to repeat the same line verbatim.
     */
    @SerializedName("frequency_penalty")
    var frequencyPenalty: Double? = null
}