package com.longkd.chatgpt_openai.base.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryFileResponse(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("summaryText") val summaryText: String?,
    @SerializedName("summaryContent") val summaryContent: String?,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("suggestList") val suggestList: List<String>? = null
): Parcelable
