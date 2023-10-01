package com.longkd.chatgpt_openai.base.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class SingleTouchLayout : FrameLayout {

    companion object {
        private const val BLOCKING_TIME = 500L
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isBlockingTouchEvent = false
    var enableSingleTouch = true

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!enableSingleTouch)
            return false
        val pointerCount = ev?.pointerCount
        if (pointerCount != null && pointerCount >= 2) {
            return true
        }
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            when (isBlockingTouchEvent) {
                true -> return false
                else -> {
                    isBlockingTouchEvent = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        isBlockingTouchEvent = false
                    }, BLOCKING_TIME)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}