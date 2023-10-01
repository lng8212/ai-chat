
package com.longkd.chatgpt_openai.open.dto.embedding

import com.longkd.chatgpt_openai.open.dto.Usage
/**
 * An object containing a response from the answer api
 *
 * https://beta.openai.com/docs/api-reference/embeddings/create
 */
class EmbeddingResult {
    /**
     * The GPT-3 model used for generating embeddings
     */
    var model: String? = null

    /**
     * The type of object returned, should be "list"
     */
    var list: String? = null

    /**
     * A list of the calculated embeddings
     */
    var data: List<Embedding>? = null

    /**
     * The API usage for this request
     */
    var usage: Usage? = null
}