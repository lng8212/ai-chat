package com.longkd.chatai.ui.custom

import android.graphics.Color
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * @Author: longkd
 * @Since: 22:11 - 29/09/2023
 */
open class MySpannable(isUnderline: Boolean) : ClickableSpan() {
    private var isUnderline = true

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
        ds.color = Color.parseColor("#00B085")
        ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun onClick(p0: View) {

    }

    init {
        this.isUnderline = isUnderline
    }
}