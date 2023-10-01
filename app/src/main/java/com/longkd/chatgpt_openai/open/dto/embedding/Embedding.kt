
package com.longkd.chatgpt_openai.open.dto.embedding

/**
 * Represents an embedding returned by the embedding api
 *
 * https://beta.openai.com/docs/api-reference/classifications/create
 */
class Embedding {
    /**
     * The type of object returned, should be "embedding"
     */
    var embedding: String? = null

    /**
     * The embedding vector
     */
    var embeddingVector: List<Double>? = null

    /**
     * The position of this embedding in the list
     */
    var index: Int? = null
}