package com.longkd.chatgpt_openai.base.customview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.flexbox.FlexboxLayout
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.databinding.LayoutCustomTopicBinding
import com.longkd.chatgpt_openai.feature.home_new.topic.DataFieldTopic
import com.longkd.chatgpt_openai.feature.home_new.topic.DataTypeTopic

class CustomTopicView : ConstraintLayout {
    private var mBinding: LayoutCustomTopicBinding? = null
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val selectedItems = HashSet<String>(HashSet())
    private var mDataType: String ? = null
    private var mOnClickSelectListener: (() -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initViews(context, attrs)
    }

    private fun initViews(context: Context?, attrs: AttributeSet?) {
        mBinding = LayoutCustomTopicBinding.inflate(layoutInflater, this, true)
    }

    fun updateView(data: DataFieldTopic? = null) {
        data?.let {
            this.mDataType = it.dataType
            mBinding?.titleView?.text = data.field1
            when(data.dataType) {
                DataTypeTopic.INPUT.name -> {
                    mBinding?.inputText?.visible()
                    mBinding?.flexbox?.gone()
                    mBinding?.icArrowDown?.gone()
                    mBinding?.inputSelect?.gone()
                }
                DataTypeTopic.MULTI_SELECT.name -> {
                    mBinding?.inputText?.gone()
                    mBinding?.flexbox?.visible()
                    mBinding?.icArrowDown?.gone()
                }
                DataTypeTopic.SELECT.name -> {
                    mBinding?.flexbox?.gone()
                    mBinding?.icArrowDown?.visible()
                    mBinding?.inputSelect?.visible()
                    mBinding?.inputSelect?.text = data.option?.firstOrNull()
                }
                else -> {
                    mBinding?.inputText?.visible()
                    mBinding?.flexbox?.gone()
                    mBinding?.icArrowDown?.visible()
                }
            }
            data.option?.forEach { str ->
                mBinding?.flexbox?.addView(TextView(context).apply {
                    text = str
                    setBackgroundResource(R.drawable.bg_border_select_box)
                    val params = FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                    )
                    val margin = 20
                    params.setMargins(0, 0, margin, margin)
                    setTextColor(ContextCompat.getColor(context, R.color.color_white_80))
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen._15sdp)
                    )
                    typeface = ResourcesCompat.getFont(context, R.font.font_regular)
                    layoutParams = params

                    setOnClickListener {
                        onItemSelected(it as TextView, str)
                    }
                })
            }
        }
        mBinding?.inputSelect?.setOnSingleClick {
            mOnClickSelectListener?.invoke()
        }
    }

    fun setOnClickSelectListener(onClick: () -> Unit) {
        this.mOnClickSelectListener = onClick
    }

    fun getDataField(): String {
        val result: String = when (mDataType) {
            DataTypeTopic.MULTI_SELECT.name -> {
                selectedItems.toString()
            }

            DataTypeTopic.SELECT.name -> {
                mBinding?.inputSelect?.text?.trim().toString()
            }

            else -> {
                mBinding?.inputText?.text?.trim().toString()
            }
        }
        return result
    }

    fun setFieldInputText(value: String) {
        mBinding?.inputSelect?.text = value
    }

    fun getTitle(): String {
        return mBinding?.titleView?.text.toString()
    }
    private fun onItemSelected(view: TextView, item: String) {
        val enable = selectedItems.contains(item)
        if (enable) {
            selectedItems.remove(item)
        } else {
            selectedItems.add(item)
        }
        view.setBackgroundResource(
            if (enable) R.drawable.bg_border_select_box
            else R.drawable.bg_border_select_box_select
        )
    }

}