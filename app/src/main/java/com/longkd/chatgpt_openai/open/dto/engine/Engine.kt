
package com.longkd.chatgpt_openai.open.dto.engine

/**
 * GPT-3 engine details
 *
 * https://beta.openai.com/docs/api-reference/retrieve-engine
 */
@Deprecated("")
class Engine {
    /**
     * An identifier for this engine, used to specify an engine for completions or searching.
     */
    var id: String? = null

    /**
     * The type of object returned, should be "engine"
     */
    var engine: String? = null

    /**
     * The owner of the GPT-3 engine, typically "openai"
     */
    var owner: String? = null

    /**
     * Whether the engine is ready to process requests or not
     */
    var ready = false
}