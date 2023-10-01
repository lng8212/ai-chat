package com.longkd.chatgpt_openai.base.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CharacterDto(val resID: Int, var isSelected: Boolean) : Parcelable {
}