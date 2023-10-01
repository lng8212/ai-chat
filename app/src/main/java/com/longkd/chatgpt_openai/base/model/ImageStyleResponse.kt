package com.longkd.chatgpt_openai.base.model

import com.google.gson.annotations.SerializedName

data class ImageStyleResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") var message: String?,
    @SerializedName("data") val data: List<ImageStyleData> ? = null
)

data class ImageStyleData(
    @SerializedName("name") val name: String ? = null,
    @SerializedName("url") val url: String ? = null,
    var isSelect: Boolean ? = false
) {
    fun convertNameStyle() = name?.replace("_", " ")?.replace(".jpg", "")
}
