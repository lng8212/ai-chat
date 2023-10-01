package com.longkd.chatgpt_openai.open.dto.finetune

/**
 * An object representing an event in the lifecycle of a fine-tuning job
 *
 * https://beta.openai.com/docs/api-reference/fine-tunes
 */
class FineTuneEvent {
    /**
     * The type of object returned, should be "fine-tune-event".
     */
    var fineTuneEvent: String? = null

    /**
     * The creation time in epoch seconds.
     */
    var createdAt: Long? = null

    /**
     * The log level of this message.
     */
    var level: String? = null

    /**
     * The event message.
     */
    var message: String? = null
}