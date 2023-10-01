

package com.longkd.chatgpt_openai.open.dto

class BmCompletionRequest {
    var time: String = ""
    var model: String = ""
    var prompt: String = ""
    var temperature: Int = 0
    var max_token: Int = 200
    var echo: Boolean = false

}