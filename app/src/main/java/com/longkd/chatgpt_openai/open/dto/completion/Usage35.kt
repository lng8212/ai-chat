
package com.longkd.chatgpt_openai.open.dto.completion

import com.google.gson.annotations.SerializedName

data class Usage35 (

  @SerializedName("prompt_tokens"     ) var promptTokens     : Int? = null,
  @SerializedName("completion_tokens" ) var completionTokens : Int? = null,
  @SerializedName("total_tokens"      ) var totalTokens      : Int? = null

)