package com.longkd.base_android.utils

/**
 * @Author: longkd
 * @Since: 23:00 - 11/08/2023
 */
object ColorUtils {
    fun fromArgb(alpha: Int = 255, red: Int = 255, green: Int = 255, blue: Int = 255): Int {
        return (alpha and 0xff shl 24) or (red and 0xff shl 16) or (green and 0xff shl 8) or (blue and 0xff)
    }

    fun getAlphaFrom(color: Int): Int {
        return (color shr 24) and 0xff
    }

    fun getRedFrom(color: Int): Int {
        return (color shr 16) and 0xff
    }

    fun getGreenFrom(color: Int): Int {
        return (color shr 8) and 0xff
    }

    fun getBlueFrom(color: Int): Int {
        return color and 0xff
    }

}