package com.longkd.chatgpt_openai.base.widget


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.databinding.ItemSettingBinding

class ItemSetting : ConstraintLayout {
    private var mBinding: ItemSettingBinding? = null
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mOnCheckedChangeListener: ((value: Boolean) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initViews(context, attrs)
    }

    private var isChecked = false

    private fun initViews(context: Context?, attrs: AttributeSet?) {
        mBinding = ItemSettingBinding.inflate(layoutInflater, this, true)
        if (null != attrs) {
            val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.ItemSetting)
            val textTitle =
                typedArray?.getString(R.styleable.ItemSetting_Its_title)

            if (textTitle != null) {
                setTextTitle(textTitle)
            }
            val viewIcon =
                typedArray?.getResourceId(R.styleable.ItemSetting_Its_icon, 0)

            if (viewIcon != null) {
                mBinding?.itemSettingIcon?.setImageResource(viewIcon)
            }
            val viewStyle = typedArray?.getInt(R.styleable.ItemSetting_Its_style, 0)
            if (viewStyle == 0) {
                mBinding?.itemSettingSw?.gone()
                mBinding?.itemSettingTitle?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right, 0)
            } else {
                mBinding?.itemSettingTitle?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                mBinding?.itemSettingSw?.visible()
                mBinding?.itemSettingSw?.setOnSingleClick {
                    setSwitchEnable(!isChecked)
                    mOnCheckedChangeListener?.invoke(isChecked)
                    CommonSharedPreferences.getInstance()
                        .putBoolean(Constants.ENABLE_ANIMATE_TEXT, isChecked)
                }
            }
            typedArray?.recycle()
        }
    }

    fun setTextTitle(value: String) {
        mBinding?.itemSettingTitle?.text = value
    }

    fun onSwitchChange(callback: ((value: Boolean) -> Unit)? = null) {
        mOnCheckedChangeListener = callback
    }

    fun setSwitchEnable(value: Boolean) {
        isChecked = value
        if (value) {
            mBinding?.itemSettingSw?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_switch_on))
        } else {
            mBinding?.itemSettingSw?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_switch_off_disabled))
        }
    }


}