
package com.longkd.chatgpt_openai.open.dto.generate

import com.google.gson.annotations.SerializedName


data class Attachments (

  @SerializedName("id"           ) var id          : String? = null,
  @SerializedName("filename"     ) var filename    : String? = null,
  @SerializedName("size"         ) var size        : Int?    = null,
  @SerializedName("url"          ) var url         : String? = null,
  @SerializedName("proxy_url"    ) var proxyUrl    : String? = null,
  @SerializedName("width"        ) var width       : Int?    = null,
  @SerializedName("height"       ) var height      : Int?    = null,
  @SerializedName("content_type" ) var contentType : String? = null

)