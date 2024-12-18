package com.longkd.chatgpt_openai.base.widget

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.base.util.LoggerUtil

class SpeedyLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    ) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        try {
            val linearSmoothScroller: LinearSmoothScroller =
                object : LinearSmoothScroller(recyclerView.context) {

                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                    }
                }
            linearSmoothScroller.targetPosition = position
            startSmoothScroll(linearSmoothScroller)
        } catch (ex: Exception) {
            LoggerUtil.e("smoothScrollToPosition, ${ex.stackTraceToString()}")
        }
    }

    companion object {
        private const val MILLISECONDS_PER_INCH = 50f //default is 25f (bigger = slower)
    }
}