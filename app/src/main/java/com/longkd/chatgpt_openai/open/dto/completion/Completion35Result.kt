
package com.longkd.chatgpt_openai.open.dto.completion

import com.google.gson.annotations.SerializedName

class Completion35Result {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("created")
    var created: Int? = null
    @SerializedName("choices")
    var choices: ArrayList<Choices35> = arrayListOf()
}