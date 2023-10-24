package com.longkd.chatgpt_openai.base.widget

import BottomFloatingType
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.longkd.chatgpt_openai.base.util.OnSingleClickListener
import com.longkd.chatgpt_openai.databinding.FragmentBottomBarFloatingBinding

class BottomBarFloating : ConstraintLayout {
    var mBinding: FragmentBottomBarFloatingBinding? = null
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mCurrentType: BottomFloatingType = BottomFloatingType.TYPE_HOME

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initViews(context, attrs)
    }

    var onItemSelected: ((type: BottomFloatingType) -> Unit)? = null

    private fun initViews(context: Context, attrs: AttributeSet?) {
        mBinding = FragmentBottomBarFloatingBinding.inflate(layoutInflater, this, true)


        val textSize = measuredHeight * 0.17857143f
        if (textSize > 0) {
            mBinding?.btfFirstTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            mBinding?.btfSecondTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            mBinding?.btfThreeTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            mBinding?.btfFourTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
        mBinding?.btfFirstAction?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (mCurrentType == BottomFloatingType.TYPE_TOPIC)
                    return
                disableState(mCurrentType)
                mCurrentType = BottomFloatingType.TYPE_TOPIC
                enableState(BottomFloatingType.TYPE_TOPIC)
                onItemSelected?.invoke(BottomFloatingType.TYPE_TOPIC)
            }

        })
        mBinding?.btfSecondAction?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (mCurrentType == BottomFloatingType.TYPE_HOME)
                    return
                disableState(mCurrentType)
                mCurrentType = BottomFloatingType.TYPE_HOME
                enableState(BottomFloatingType.TYPE_HOME)
                onItemSelected?.invoke(BottomFloatingType.TYPE_HOME)
            }

        })

        mBinding?.btfThreeAction?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (mCurrentType == BottomFloatingType.TYPE_ART)
                    return
                disableState(mCurrentType)
                mCurrentType = BottomFloatingType.TYPE_ART
                enableState(BottomFloatingType.TYPE_ART)
                onItemSelected?.invoke(BottomFloatingType.TYPE_ART)
            }

        })
        mBinding?.btfFourAction?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (mCurrentType == BottomFloatingType.TYPE_SETTING)
                    return
                disableState(mCurrentType)
                mCurrentType = BottomFloatingType.TYPE_SETTING
                enableState(BottomFloatingType.TYPE_SETTING)
                onItemSelected?.invoke(BottomFloatingType.TYPE_SETTING)
            }

        })

    }

    fun setCurrentTab(currentTabType: BottomFloatingType) {
        if (mCurrentType == currentTabType)
            return
        disableState(mCurrentType)
        mCurrentType = currentTabType
        enableState(currentTabType)
    }

    fun disableState(type: BottomFloatingType) {
        when (type) {
            BottomFloatingType.TYPE_TOPIC -> {
                mBinding?.btfFirstIcon?.isSelected = false
                mBinding?.btfFirstTitle?.isSelected = false
                mBinding?.btfFirstTitle?.typeface = Typeface.DEFAULT
            }
            BottomFloatingType.TYPE_HOME -> {
                mBinding?.btfSecondIcon?.isSelected = false
                mBinding?.btfSecondTitle?.isSelected = false
                mBinding?.btfSecondTitle?.typeface = Typeface.DEFAULT
            }
            BottomFloatingType.TYPE_ART -> {
                mBinding?.btfThreeIcon?.isSelected = false
                mBinding?.btfThreeTitle?.isSelected = false
                mBinding?.btfThreeTitle?.typeface = Typeface.DEFAULT
            }
            BottomFloatingType.TYPE_SETTING -> {
                mBinding?.btfFourIcon?.isSelected = false
                mBinding?.btfFourTitle?.isSelected = false
                mBinding?.btfFourTitle?.typeface = Typeface.DEFAULT
            }

            else -> {}
        }
    }

    fun enableState(type: BottomFloatingType) {
        when (type) {
            BottomFloatingType.TYPE_TOPIC -> {
                mBinding?.btfFirstIcon?.isSelected = true
                mBinding?.btfFirstTitle?.isSelected = true
                mBinding?.btfFirstTitle?.typeface = Typeface.DEFAULT_BOLD
            }
            BottomFloatingType.TYPE_HOME -> {
                mBinding?.btfSecondIcon?.isSelected = true
                mBinding?.btfSecondTitle?.isSelected = true
                mBinding?.btfSecondTitle?.typeface = Typeface.DEFAULT_BOLD
            }
            BottomFloatingType.TYPE_ART -> {
                mBinding?.btfThreeIcon?.isSelected = true
                mBinding?.btfThreeTitle?.isSelected = true
                mBinding?.btfThreeTitle?.typeface = Typeface.DEFAULT_BOLD
            }
            BottomFloatingType.TYPE_SETTING -> {
                mBinding?.btfFourIcon?.isSelected = true
                mBinding?.btfFourTitle?.isSelected = true
                mBinding?.btfFourTitle?.typeface = Typeface.DEFAULT_BOLD
            }
            else -> {}
        }
    }
}
