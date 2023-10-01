package com.longkd.chatgpt_openai.base.customview

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.longkd.chatgpt_openai.R

class ToastView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    AppCompatTextView(context, attrs, defStyle) {
    private lateinit var showAnimation: Animation
    private lateinit var hideAnimation: Animation

    private var value: String? = null
    private val myHandler = Handler() // default view handler not working correctly
    private lateinit var runnable: Runnable

    enum class State {
        GONE, FADEIN, SHOWING, FADEOUT
    }

    private var currentState = State.GONE

    init {
        gravity = Gravity.CENTER
        visibility = View.GONE
        val padding = resources.getDimension(R.dimen._12sdp).toInt()
        setPadding(padding, padding / 2, padding, padding / 2)
        setTextColor(ContextCompat.getColor(context, R.color.color_white))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        val typefaceBlod: Typeface? = ResourcesCompat.getFont(context, R.font.font_medium)
        typeface = typefaceBlod
        val style = context.obtainStyledAttributes(attrs, R.styleable.CustomToastView, defStyle, 0)
        val showTime = style.getInteger(R.styleable.CustomToastView_show_time, SHOW_TIME)
        initAnimation(showTime)
        style.recycle()

    }

    private fun initAnimation(showTime: Int) {
        hideAnimation = AlphaAnimation(1f, 0f)
        hideAnimation.duration = ANIMATION_DURATION

        showAnimation = AlphaAnimation(0f, 1f)
        showAnimation.duration = ANIMATION_DURATION

        runnable = Runnable {
            this.value = null
            startAnimation(hideAnimation)
        }

        hideAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                // do nothing
            }

            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
                currentState = State.GONE
                value = null
            }

            override fun onAnimationStart(animation: Animation?) {
                myHandler.removeCallbacks(runnable)
                currentState = State.FADEOUT
            }
        })

        showAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                // do nothing
            }

            override fun onAnimationEnd(animation: Animation?) {
                if (value != null) {
                    currentState = State.SHOWING
                    myHandler.removeCallbacks(runnable)
                    myHandler.postDelayed(runnable, showTime.toLong())
                } else {
                    visibility = GONE
                    currentState = State.GONE
                    myHandler.removeCallbacks(runnable)
                }
            }

            override fun onAnimationStart(animation: Animation?) {
                text = value
                myHandler.removeCallbacks(runnable)
                currentState = State.FADEIN
            }
        })
    }

    fun showText(@StringRes stringRes: Int, backgroundColor: Int? = null) {
        showText(context.getString(stringRes), backgroundColor)
    }

    fun showText(value: String, backgroundColor: Int? = null) {
        this.value = value
        when (currentState) {
            State.SHOWING -> {
                myHandler.removeCallbacks(runnable)
                startAnimation(showAnimation)
            }
            State.FADEIN -> {
                clearAnimation()
                startAnimation(showAnimation)
            }
            State.GONE -> {
                visibility = View.VISIBLE
                startAnimation(showAnimation)
            }
            State.FADEOUT -> {
                clearAnimation()
            }
        }
        if (backgroundColor != null) {
            if (backgroundColor == R.color.color_1A3239) {
                setBackgroundResource(R.drawable.bg_rounded_main_16)
            }
        }
    }

    fun hideText(immediately: Boolean = false) {
        value = null
        when (currentState) {
            State.SHOWING -> {
                myHandler.removeCallbacks(runnable)
                if (immediately) {
                    visibility = GONE
                    currentState = State.GONE
                } else startAnimation(hideAnimation)
            }
            State.FADEIN -> {
                clearAnimation() // onAnimationEnd will be called
                if (!immediately) startAnimation(hideAnimation)
            }
            State.FADEOUT -> if (immediately) clearAnimation() // onAnimationEnd will be called
            State.GONE -> { // do nothing
            }
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 750L
        private const val SHOW_TIME = 3500
    }
}