package com.longkd.chatgpt_openai.open.dto.model

/**
 * GPT-3 model permissions
 * I couldn't find documentation for the specific permissions, and I've elected to leave them undocumented rather than
 * write something incorrect.
 *
 * https://beta.openai.com/docs/api-reference/models
 */
class Permission {
    /**
     * An identifier for this model permission
     */
    var id: String? = null

    /**
     * The type of object returned, should be "model_permission"
     */
    var modelPermission: String? = null

    /**
     * The creation time in epoch seconds.
     */
    var created: Long = 0
    var allowCreateEngine = false
    var allowSampling = false
    var allowLogProbs = false
    var allowSearchIndices = false
    var allowView = false
    var allowFineTuning = false
    var organization: String? = null
    var group: String? = null
    var isBlocking = false
}