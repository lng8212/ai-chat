package com.longkd.base_android.base.toolbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * @Author: longkd
 * @Since: 20:18 - 12/08/2023
 */
data class ToolbarConfiguration(
    @StringRes
    val titleResId: Int? = null,
    @DrawableRes
    val startIconResId: Int? = null,
    val startIconClick: (() -> Unit)? = null,
    @DrawableRes
    val endIconResId: Int? = null,
    val endIconClick: (() -> Unit)? = null
)
