package com.longkd.chatgpt_openai.base.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SummaryData(
    @SerializedName("uri")
    val uri: String ? = null,
    @SerializedName("fileName")
    val fileName: String ? = null,
    var isSelect: Boolean = false
): Parcelable
