package com.longkd.chatgpt_openai.feature.art

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class StyleArtDto(val resID: Int, val name : String, val modelId: String = "midjourney" ,var isSelected: Boolean = false) : Parcelable {
}