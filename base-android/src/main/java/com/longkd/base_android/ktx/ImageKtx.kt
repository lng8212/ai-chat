package com.longkd.base_android.ktx

import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView

/**
 * @Author: longkd
 * @Since: 20:35 - 12/08/2023
 */
fun ImageView.setImageDrawableWithAnimation(drawable: Drawable?, duration: Int = 300) {
    val currentDrawable = getDrawable()
    if (currentDrawable == null) {
        setImageDrawable(drawable)
        return
    }
    val transitionDrawable = TransitionDrawable(
        arrayOf(
            currentDrawable,
            drawable
        )
    )
    setImageDrawable(transitionDrawable)
    transitionDrawable.isCrossFadeEnabled = true
    transitionDrawable.startTransition(duration)
}