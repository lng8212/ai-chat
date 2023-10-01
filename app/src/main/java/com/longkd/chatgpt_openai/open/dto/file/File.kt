package com.longkd.chatgpt_openai.open.dto.file

/**
 * A file uploaded to OpenAi
 *
 * https://beta.openai.com/docs/api-reference/files
 */
class File {
    /**
     * The unique id of this file.
     */
    var id: String? = null

    /**
     * The type of object returned, should be "file".
     */
    var file: String? = null

    /**
     * File size in bytes.
     */
    var bytes: Long? = null

    /**
     * The creation time in epoch seconds.
     */
    var createdAt: Long? = null

    /**
     * The name of the file.
     */
    var filename: String? = null

    /**
     * Description of the file's purpose.
     */
    var purpose: String? = null
}