/*
 * Created by longkd on 10/24/23, 9:42 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/24/23, 9:42 PM
 */

package com.longkd.chatgpt_openai.base.util

/**
 * @Author: longkd
 * @Since: 21:42 - 24/10/2023
 */
object DataUtils {

    fun getListSize() = listOf(
        Size("256x256", true),
        Size("512x512"),
        Size("1024x1024"),
        Size("1024x1792"),
        Size("1792x1024")
    )
}

data class Size(val size: String, var isSelected: Boolean = false)
