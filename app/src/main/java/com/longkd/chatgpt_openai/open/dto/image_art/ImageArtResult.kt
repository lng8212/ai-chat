

package com.longkd.chatgpt_openai.open.dto.image_art

import com.longkd.chatgpt_openai.open.dto.generate.Attachments
import com.longkd.chatgpt_openai.open.dto.generate.ComponentsType
import com.google.gson.annotations.SerializedName


data class ImageArtResult (

  @SerializedName("id"                ) var id               : String?                = null,
  @SerializedName("content"           ) var content          : String?                = null,
  @SerializedName("attachments"       ) var attachments      : ArrayList<Attachments> = arrayListOf(),
  @SerializedName("timestamp"         ) var timestamp        : String?                = null,
  @SerializedName("components"        ) var components       : ArrayList<ComponentsType>  = arrayListOf(),
  @SerializedName("message_reference" ) var messageReference : MessageReference?      = MessageReference()

)