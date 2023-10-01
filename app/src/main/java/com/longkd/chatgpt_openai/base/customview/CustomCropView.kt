package com.longkd.chatgpt_openai.base.customview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.longkd.chatgpt_openai.base.customview.CropImageView
import com.longkd.chatgpt_openai.base.customview.CropOverlayView

class CustomCropView: FrameLayout {
    private val imageView: CropImageView? = null
    private val overlayView: CropOverlayView? = null

    constructor(context: Context): super(context) {}
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {}
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {}


}