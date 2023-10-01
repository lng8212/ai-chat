package com.longkd.chatgpt_openai.base.model

import com.longkd.chatgpt_openai.R


enum class LanguageItem(val value: String, val code: String, val flag : Int) {
    ENGLISH("English", "en", R.drawable.ic_language_en),
    FINNISH("Finnish", "fi", R.drawable.ic_language_fi),
    GERMAN("German", "de", R.drawable.ic_language_de),
    HINDI("Hindi", "hi", R.drawable.ic_language_hi),
    KOREAN("Korean", "ko", R.drawable.ic_language_kr),
    NORWEGIAN("Norwegian", "nb", R.drawable.ic_language_nb),
    PORTUGUESE("Portuguese", "pt", R.drawable.ic_language_pt),
    SPANISH("Spanish", "es", R.drawable.ic_language_es),
    JAPANESE("Japanese", "ja", R.drawable.ic_language_jp),
    CHINESE("Chinese", "zh", R.drawable.ic_language_zh),
}