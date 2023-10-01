

package com.longkd.chatgpt_openai.open.dto.generate

import com.google.gson.annotations.SerializedName


data class Components (

  @SerializedName("type"      ) var type     : Int?    = null,
  @SerializedName("style"     ) var style    : Int?    = null,
  @SerializedName("label"     ) var label    : String? = null,
  @SerializedName("custom_id" ) var customId : String? = null

)