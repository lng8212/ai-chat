package com.longkd.base_android.ktx

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import com.longkd.base_android.utils.AnimationHelper
import com.longkd.base_android.utils.OnSingleClickListener

/**
 * @Author: longkd
 * @Since: 10:30 - 12/08/2023
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.height(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    layoutParams = params
    return this
}

fun View.width(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    layoutParams = params
    return this
}

fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    params.width = width
    layoutParams = params
    return this
}

fun View.animateWidth(
    toValue: Int,
    duration: Long = 200,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(width, toValue).apply {
            addUpdateListener {
                width(it.animatedValue as Int)
                action?.invoke(it.animatedFraction)
            }
            listener?.let {
                addListener(it)
            }
            setDuration(duration)
            start()
        }
    }
    return animator
}

fun View.animateHeight(
    toValue: Int,
    duration: Long = 200,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(width, toValue).apply {
            addUpdateListener {
                height(it.animatedValue as Int)
                action?.invoke(it.animatedFraction)
            }
            listener?.let {
                addListener(it)
            }
            setDuration(duration)
            start()
        }
    }
    return animator
}

fun View.animateWidthAndHeight(
    toWidth: Int,
    toHeight: Int,
    duration: Long = 200,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        animator = ValueAnimator.ofInt(width, toWidth).apply {
            addUpdateListener {
                widthAndHeight(
                    it.animatedValue as Int,
                    evaluator.evaluate(it.animatedFraction, startHeight, toHeight)
                )
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

fun View.onSingleClickAnim(action: ((v: View?) -> Unit)) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View?) {
            AnimationHelper.scaleAnimation(v, animationListener = {
                kotlin.runCatching {
                    action.invoke(v)
                }
            })
        }

    })
}

fun View.setOnSingleClick(action: ((v: View?) -> Unit)) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View?) {
            action.invoke(v)
        }
    })
}