package com.longkd.base_android.ktx

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author: longkd
 * @Since: 10:20 - 12/08/2023
 */
fun RecyclerView.setHorizontalViewPort(viewport: Float) {
    val ceil = kotlin.math.ceil(viewport)
    val floor = kotlin.math.floor(viewport)
    val countSpace = if (ceil == floor && ceil == viewport) {
        viewport.toInt()
    } else {
        viewport.toInt() - 1
    }
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = ((width - view.layoutParams.width * viewport) / countSpace).toInt()
            super.getItemOffsets(outRect, view, parent, state)
        }
    })
}