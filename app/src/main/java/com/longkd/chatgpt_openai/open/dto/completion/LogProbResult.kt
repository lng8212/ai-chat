
package com.longkd.chatgpt_openai.open.dto.completion

/**
 * Log probabilities of different token options
 * Returned if [CompletionRequest.logprobs] is greater than zero
 *
 * https://beta.openai.com/docs/api-reference/create-completion
 */
class LogProbResult {
    /**
     * The tokens chosen by the completion api
     */
    var tokens: List<String>? = null

    /**
     * The log probability of each token in [tokens]
     */
    var tokenLogprobs: List<Double>? = null

    /**
     * A map for each index in the completion result.
     * The map contains the top [CompletionRequest.logprobs] tokens and their probabilities
     */
    var topLogprobs: List<Map<String, Double>>? = null

    /**
     * The character offset from the start of the returned text for each of the chosen tokens.
     */
    var textOffset: List<Int>? = null
}