
package com.longkd.chatgpt_openai.open.dto.generate

import com.google.gson.annotations.SerializedName


data class GenerateArtResult (

  @SerializedName("id"          ) var id          : String?                = null,
  @SerializedName("content"     ) var content     : String?                = null,
  @SerializedName("attachments" ) var attachments : List<Attachments>?= null,
  @SerializedName("timestamp"   ) var timestamp   : String?                = null,
  @SerializedName("components"  ) var components  : List<ComponentsType>  ?= null

)