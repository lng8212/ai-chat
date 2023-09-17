package com.longkd.base_android.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.view.setPadding

/**
 * @Author: longkd
 * @Since: 22:59 - 11/08/2023
 */
internal object ViewUtils {

    const val LOADING_DEFAULT = 9999

    fun getDefaultLoadingView(context: Context): FrameLayout {
        return FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            isClickable = true
            isFocusable = true
            setBackgroundColor(ColorUtils.fromArgb(126, 150, 150, 150))

            val backgroundLoadingView = FrameLayout(context)
            backgroundLoadingView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            backgroundLoadingView.setPadding(18)
            backgroundLoadingView.background = GradientDrawable().apply {
                cornerRadius = 32f
            }
            backgroundLoadingView.backgroundTintList =
                ColorStateList.valueOf(ColorUtils.fromArgb(255, 50, 50, 50))
            backgroundLoadingView.addView(ProgressBar(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
            })
            addView(backgroundLoadingView)
            elevation = 100f
        }
    }
}