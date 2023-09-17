package com.longkd.chatai.data.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("data")
    val data: List<com.longkd.chatai.data.api.response.UserResponse.Data>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("support")
    val support: com.longkd.chatai.data.api.response.UserResponse.Support,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPages")
    val totalPages: Int
) {
    data class Data(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("last_name")
        val lastName: String
    )

    data class Support(
        @SerializedName("text")
        val text: String,
        @SerializedName("url")
        val url: String
    )
}