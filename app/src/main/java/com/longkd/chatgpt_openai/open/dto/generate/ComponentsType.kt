package com.longkd.chatgpt_openai.open.dto.generate

import com.google.gson.annotations.SerializedName


data class ComponentsType (
  @SerializedName("type"  ) var type       : Int?                  = null,
  @SerializedName("components" ) var components : ArrayList<Components>? = null
)