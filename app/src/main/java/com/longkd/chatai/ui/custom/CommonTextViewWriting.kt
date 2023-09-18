package com.longkd.chatai.ui.custom

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * @Author: longkd
 * @Since: 20:40 - 18/09/2023
 */
class CommonTextViewWriting : AppCompatTextView {
    private var mText: CharSequence? = null
    private var mTextDefault: CharSequence? = null
    private var mIndex = 0
    private var mDelay: Long = 100L //Default 100ms delay
    private var mIsTextAnimate = false
    private var mIsAddText = false
    var mOnAnimateFinished: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context, attrs
    ) {
        initView()
    }

    private fun initView() {
        setTextIsSelectable(true)
    }

    private val mHandler = Handler(Looper.getMainLooper())
    private val characterAdder: Runnable = object : Runnable {
        override fun run() {
            kotlin.runCatching {
                text = if (mIsAddText)
                    mTextDefault.toString() + mText?.subSequence(0, mIndex++)
                else
                    mText?.subSequence(0, mIndex++)
            }
            if (mIndex <= (mText?.length ?: 0)) {
                mHandler.postDelayed(this, mDelay)
            } else if (mIsTextAnimate && !mText.isNullOrEmpty()) {
                mIsTextAnimate = false
                mOnAnimateFinished?.invoke()
                completeText()
            }
        }
    }

    private fun completeText() {
        try {
            handler.removeCallbacks(characterAdder)
            mIndex = 0
            mIsTextAnimate = false
        } catch (_: Exception) {
        }
    }

    fun setTextNormal(text: CharSequence?) {
        mText = text
    }

    fun animateText(text: CharSequence?) {
        setText("")
        mText = text
        mIndex = 0
        mHandler.removeCallbacks(characterAdder)
        mHandler.postDelayed(characterAdder, mDelay)
        mIsAddText = false
        mIsTextAnimate = true
    }

    fun setCharacterDelay(millis: Long) {
        mDelay = millis
    }

    fun stopAnimateText() {
        mHandler.removeCallbacks(characterAdder)
        text = if (mIsAddText)
            mTextDefault.toString() + mText.toString()
        else mText
    }

    fun updateIndex(index: Int) {
        if (index > mIndex)
            mIndex = index
    }

    fun animateTextAdd(textMore: CharSequence?) {
        mTextDefault = text
        mText = textMore
        mIndex = 0
        mHandler.removeCallbacks(characterAdder)
        mHandler.postDelayed(characterAdder, mDelay)
        mIsAddText = true
        mIsTextAnimate = true
    }
}