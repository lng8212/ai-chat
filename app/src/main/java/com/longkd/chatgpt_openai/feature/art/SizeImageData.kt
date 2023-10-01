package com.longkd.chatgpt_openai.feature.art

import androidx.annotation.DrawableRes
import com.longkd.chatgpt_openai.R
import java.lang.IllegalArgumentException

enum class SizeImageData(
    val title: Int,
    val width: Int,
    val height: Int,
) {
    SQUARE(R.string.str_square, 512, 512),
    ORIGINAL(R.string.str_original,680, 512),
    WALLPAPER(R.string.str_wallpaper, 512 , 912);

    companion object {
        fun fromString(value: String?): SizeImageData? {
            return try {
                value?.let { valueOf(value) }
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}