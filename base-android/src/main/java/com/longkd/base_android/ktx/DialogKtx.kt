package com.longkd.base_android.ktx

import android.app.Dialog
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * @Author: longkd
 * @Since: 10:18 - 12/08/2023
 */
fun Dialog.setTransparentBackground() {
    if (this is BottomSheetDialog) {
        val bottomSheet = findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.setBackgroundResource(android.R.color.transparent)
    } else {
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}

/**
 * set ratio for width and height of dialog.
 * width <=> screen's width
 * height <=> screen's height
 */
fun Dialog.setLayoutParamsRatio(ratioWidth: Float? = null, ratioHeight: Float? = null) {
    val displayMetrics = context.resources.displayMetrics
    val widthDialog =
        if (ratioWidth != null) displayMetrics.widthPixels * ratioWidth
        else ViewGroup.LayoutParams.WRAP_CONTENT
    val heightDialog =
        if (ratioHeight != null) displayMetrics.heightPixels * ratioHeight
        else ViewGroup.LayoutParams.WRAP_CONTENT
    window?.setLayout(widthDialog.toInt(), heightDialog.toInt())
}

fun Dialog.showWithTransparentBackground() {
    setTransparentBackground()
    show()
    setLayoutParamsRatio(0.9f)
}