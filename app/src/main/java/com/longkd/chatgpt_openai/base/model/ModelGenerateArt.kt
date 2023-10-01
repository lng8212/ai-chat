package com.longkd.chatgpt_openai.base.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelGenerateArt(
    val modelId: Int,
    val modelTitle: String,
    val modelDes: String,
    @DrawableRes val drawId: Int
): Parcelable
