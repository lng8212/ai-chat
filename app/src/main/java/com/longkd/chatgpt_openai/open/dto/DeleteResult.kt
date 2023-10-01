package com.longkd.chatgpt_openai.open.dto

/**
 * A response when deleting an object
 */
class DeleteResult {
    /**
     * The id of the object.
     */
    var id: String? = null

    /**
     * The type of object deleted, for example "file" or "model"
     */
    var model: String? = null

    /**
     * True if successfully deleted
     */
    var deleted = false
}