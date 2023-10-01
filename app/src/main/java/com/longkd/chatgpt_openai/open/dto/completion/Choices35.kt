
package com.longkd.chatgpt_openai.open.dto.completion

import com.google.gson.annotations.SerializedName

data class Choices35 (
  @SerializedName("index"         ) var index        : Int?     = null,
  @SerializedName("message"       ) var message      : Message35Request? = Message35Request(),
  @SerializedName("finish_reason" ) var finishReason : String?  = null
)