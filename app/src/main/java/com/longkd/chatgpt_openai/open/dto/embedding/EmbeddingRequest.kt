
package com.longkd.chatgpt_openai.open.dto.embedding

/**
 * Creates an embedding vector representing the input text.
 *
 * https://beta.openai.com/docs/api-reference/embeddings/create
 */
class EmbeddingRequest {
    /**
     * The name of the model to use.
     * Required if using the new v1/embeddings endpoint.
     */
    var model: String? = null

    /**
     * Input text to get embeddings for, encoded as a string or array of tokens.
     * To get embeddings for multiple inputs in a single request, pass an array of strings or array of token arrays.
     * Each input must not exceed 2048 tokens in length.
     *
     *
     * Unless your are embedding code, we suggest replacing newlines (\n) in your input with a single space,
     * as we have observed inferior results when newlines are present.
     */
    var input: List<String> = listOf()

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    var user: String? = null
}