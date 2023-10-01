package com.longkd.chatgpt_openai.base.widget.header


import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.OnSingleClickListener
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentHeaderBinding

class BaseHeaderView : ConstraintLayout {
    var binding: FragmentHeaderBinding? = null
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initViews(context, attrs)
    }

    //    var textSwitcher :TextSwitcher? =null
    private fun initViews(context: Context?, attrs: AttributeSet?) {
        binding = FragmentHeaderBinding.inflate(layoutInflater, this, true)
    }


    fun setLeftAction(leftAction: CommonAction, @DrawableRes leftImgRes: Int? = null) {
        binding?.baseHeaderBtnLeft?.visibility = View.VISIBLE
        binding?.baseHeaderBtnLeft?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                leftAction.action?.invoke()
            }
        })
        if (leftImgRes != null) {
            kotlin.runCatching {
                binding?.baseHeaderBtnLeft?.setImageResource(leftImgRes)
            }
        }
    }

    fun setRightAction(leftAction: CommonAction, @DrawableRes leftImgRes: Int? = null) {
        binding?.baseHeaderBtnRight?.visibility = View.VISIBLE
        binding?.baseHeaderBtnRight?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                leftAction.action?.invoke()
            }
        })
        if (leftImgRes != null) {
            kotlin.runCatching {
                binding?.baseHeaderBtnRight?.setImageResource(leftImgRes)
            }
        }
    }

    fun setLeftIcon(@DrawableRes leftImgRes: Int? = null) {
        if (leftImgRes != null) {
            binding?.baseHeaderBtnLeft?.setImageResource(leftImgRes)
        }
    }

    fun setRightIcon(@DrawableRes rightImgRes: Int? = null) {
        if (rightImgRes != null) {
            binding?.baseHeaderBtnRight?.visibility = View.VISIBLE
            binding?.baseHeaderBtnRight?.setImageResource(rightImgRes)
        } else binding?.baseHeaderBtnRight?.visibility = View.GONE
    }

    fun setVisibleLeft(isVisible: Boolean) {
        if (isVisible)
            binding?.baseHeaderBtnLeft?.visibility = View.VISIBLE
        else binding?.baseHeaderBtnLeft?.visibility = View.GONE
    }

    fun setVisibleTitle(isVisible: Boolean) {
        if (isVisible)
            binding?.baseHeaderTvTitle?.visibility = View.VISIBLE
        else binding?.baseHeaderTvTitle?.visibility = View.GONE
    }

    //
    fun setTitle(title: String) {
        setTextHtml(binding?.baseHeaderTvTitle, title)
//        textSwitcher?.setText(title)
    }

    fun setTitleColor(color: Int) {
        kotlin.runCatching {
            binding?.baseHeaderTvTitle?.setTextColor(ContextCompat.getColor(context, color))
        }
    }

    fun getTitleText(): String {
        return binding?.baseHeaderTvTitle?.text?.toString() ?: Strings.EMPTY
    }

    fun showHeader(isShow: Boolean = true) {
        binding?.headerContainer?.visibility = if (isShow) View.VISIBLE else View.GONE
    }


    fun setHeaderHeight(@DimenRes heightDimen: Int) {
        binding?.headerContainer?.updateLayoutParams {
            context?.resources?.getDimensionPixelOffset(heightDimen)?.let {
                height = it
            }
        }
    }

    fun setbaseHeaderTvTitleColor(@ColorRes color: Int) {
        val cl = ContextCompat.getColor(context, color)
        binding?.baseHeaderTvTitle?.setTextColor(cl)
    }

    fun setbaseHeaderTvTitleLine(line: Int) {
        binding?.baseHeaderTvTitle?.setLines(line)
        binding?.baseHeaderTvTitle?.maxLines = line
    }

    fun setIconRightAction(@DrawableRes rightImgRes: Int? = null) {
        if (rightImgRes != null) {
            binding?.baseHeaderBtnRight?.setImageResource(rightImgRes)
        }
    }


    fun setTitleStart(titleAction: CommonAction, @DrawableRes rightImgRes: Int? = null) {
        binding?.baseHeaderTvTitle?.apply {
            val newParam = layoutParams as? ConstraintLayout.LayoutParams
            newParam?.endToEnd = LayoutParams.UNSET
            newParam?.height = LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER_HORIZONTAL
            newParam?.endToStart = LayoutParams.UNSET
            if (rightImgRes != null) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, rightImgRes, 0)
            }
            newParam?.let {
                layoutParams = newParam
            }
        }

        binding?.baseHeaderTvTitle?.setOnSingleClick {
            titleAction.action?.invoke()
        }
    }

    fun setTextHtml(textView: TextView?, value: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView?.text =
                    Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT)
            } else {
                textView?.text = Html.fromHtml(value)
            }
        } catch (ex: Exception) {

        }
    }

    fun changeTitleTextSize() {
        resources?.getDimension(R.dimen._20sdp)?.let {
            binding?.baseHeaderTvTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
    }

    fun setHeaderHomeType() {
        resources?.getDimension(R.dimen._18sdp)?.let {
            binding?.baseHeaderTvTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
        binding?.baseHeaderTvTitle?.typeface = Typeface.DEFAULT_BOLD
    }

    fun showCustomBtn(action: CommonAction, @DrawableRes imgRes: Int? = null) {
        binding?.baseHeaderBtnCustom?.visibility = View.VISIBLE
        binding?.baseHeaderBtnCustom?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                action.action?.invoke()
            }
        })
        if (imgRes != null) {
            binding?.baseHeaderBtnCustom?.setImageResource(imgRes)
        }
    }

    fun setCustomBtn(@DrawableRes customImgRes: Int? = null, isEnable: Boolean) {
        if (customImgRes != null) {
            binding?.baseHeaderBtnCustom?.setImageResource(customImgRes)
            if (isEnable) {
                binding?.baseHeaderBtnCustom?.isEnabled = true
                binding?.baseHeaderBtnCustom?.alpha = 1f
            } else {
                binding?.baseHeaderBtnCustom?.isEnabled = false
                binding?.baseHeaderBtnCustom?.alpha = 0.5f
            }
        }
    }

    fun setEnableBtnRightIcon(@DrawableRes customImgRes: Int? = null, isEnable: Boolean) {
        if (customImgRes != null) {
            binding?.baseHeaderBtnRight?.setImageResource(customImgRes)
            if (isEnable) {
                binding?.baseHeaderBtnRight?.isEnabled = true
                binding?.baseHeaderBtnRight?.alpha = 1f
            } else {
                binding?.baseHeaderBtnRight?.isEnabled = false
                binding?.baseHeaderBtnRight?.alpha = 0.5f
            }
        }
    }
}