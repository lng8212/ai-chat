package com.longkd.chatgpt_openai.base.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


fun View.layoutInflater(): LayoutInflater = LayoutInflater.from(this.context)

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun TextView.string() = text.toString()

fun Int.percent(percent: Int): Int {
    return (this * percent) / 100
}

fun ViewGroup.inflate(@LayoutRes view: Int): View {
    return LayoutInflater.from(this.context).inflate(view, this, false)
}

fun View.inflate(@LayoutRes view: Int): View {
    return LayoutInflater.from(this.context).inflate(view, null, false)
}

fun Context.getLinearVerticalLayoutManager(
    reverseLayout: Boolean = false
): LinearLayoutManager {
    return LinearLayoutManager(this, LinearLayoutManager.VERTICAL, reverseLayout)
}

fun Context.getLinearHorizontalLayoutManager(
    reverseLayout: Boolean = false
): LinearLayoutManager {
    return LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, reverseLayout)
}

fun Context.getGridLayoutManager(
    spanCount: Int = 3
): GridLayoutManager {
    return GridLayoutManager(this, spanCount)
}

fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

fun Context.getDisplayMetrics(): DisplayMetrics {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return DisplayMetrics().apply {
        windowManager.defaultDisplay.getMetrics(this)
    }
}

fun Context.getDisplayWidth() = getDisplayMetrics().widthPixels

fun Context.getDisplayHeight() = getDisplayMetrics().heightPixels

fun View.setScaleWithDisplay(percent: Int) {
    val scaledWith = context.getDisplayWidth().percent(percent)
    layoutParams.width = scaledWith
}

fun View.setScaleHeightDisplay(percent: Int) {
    val scaledHeight = context.getDisplayHeight().percent(percent)
    layoutParams.height = scaledHeight
}

fun View.setScaleDisplay(percent: Int) {
    setScaleHeightDisplay(percent)
    setScaleWithDisplay(percent)
}

fun View.expand(duration: Long = 0L) {
    val v = this
    val matchParentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight = v.measuredHeight

    v.layoutParams.height = 1
    v.visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            v.layoutParams.height =
                if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    if (duration == 0L) {
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
    } else {
        a.duration = duration
    }
    v.startAnimation(a)
}

fun View.collapse(duration: Long = 0L) {
    val v = this
    val initialHeight = v.measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                v.visibility = View.GONE
            } else {
                v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                v.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    if (duration == 0L) {
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
    } else {
        a.duration = duration
    }
    v.startAnimation(a)
}

fun View.rotate(fromDegree: Float, toDegree: Float, duration: Long = 100) {
    val rotate =
        RotateAnimation(
            fromDegree,
            toDegree,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
    rotate.duration = duration
    rotate.interpolator = LinearInterpolator()
    rotate.fillAfter = true
    this.startAnimation(rotate)
}

fun View.setOnSingleClick(action: ((v: View?) -> Unit)) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View?) {
            action.invoke(v)
        }

    })
}

inline fun <reified T : Number> T?.orZero(): T {
    return this ?: when (T::class) {
        Int::class -> 0
        Long::class -> 0L
        Float::class -> 0F
        Double::class -> 0.0
        else -> throw IllegalStateException("Illegal number type.")
    } as T
}

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
