package com.longkd.chatgpt_openai.feature.chat.viewholder

import android.annotation.SuppressLint
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.customview.MySpannable
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ChatItemListLeftBinding


class ChatItemLeftVH(
    mBinding: ChatItemListLeftBinding,
    var enableAnimateText: Boolean,
    private var mOnAnimateFinished: (() -> Unit)? = null,
    private var itemClickListener: ItemClickListener<ChatDetailDto>,
    private var onClickRegenerate: ((chatDto: ChatDetailDto) -> Unit)? = null,
    private var onClickViewMore: (() -> Unit)? = null
) : BaseViewHolder<ChatItemListLeftBinding>(mBinding) {
    private var isTypingRunning = false
    private var mData: ChatDetailDto? = null
    @SuppressLint("SetTextI18n")
    fun bindData(data: ChatDetailDto) {
        this.mData = data
        handleAnimateLeft()
        binding.chatItemListCopy.setOnSingleClick {
            itemClickListener.onClick(data, adapterPosition)
        }
        binding.btnRegeberate.invisible()
        binding.btnRegeberate.setOnSingleClick {
            onClickRegenerate?.invoke(data)
        }
        if (data.message != itemView.context.getString(R.string.str_title_start_chat)) {
            updateTypingAnimation(data.isTyping)
        } else {
            binding.chatItemListCopy.invisible()
            binding.btnRegeberate.invisible()
            binding.chatItemListLeftText.visible()
        }
    }

    private fun handleAnimateLeft() {
        if (enableAnimateText) {
            if (this.mData?.isLastItem == true) {
                binding.chatItemListLeftText.animateText(this.mData?.message?.trim())
            } else {
                binding.chatItemListLeftText.setTextNormal(this.mData?.message?.trim())
                binding.chatItemListLeftText.text = this.mData?.message?.trim()
            }
            if (this.mData?.isSeeMore == true && this.mData?.isLastItem == false) {
                binding.chatItemListLeftText.setSpanViewMore {
                    onClickViewMore?.invoke()
                }
            }
            binding.chatItemListLeftText.mOnAnimateFinished =  {
                mOnAnimateFinished?.invoke()
                if (this.mData?.isSeeMore == true) {
                    binding.chatItemListLeftText.setSpanViewMore {
                        onClickViewMore?.invoke()
                    }
                }
                if (this.mData?.isLastItem == true && this.mData?.isTyping == false) {
                    binding.btnRegeberate.visible()
                } else {
                    binding.btnRegeberate.invisible()
                }
            }
        } else {
            binding.chatItemListLeftText.setTextNormal(this.mData?.message?.trim())
            binding.chatItemListLeftText.text = this.mData?.message?.trim()
        }
    }

    private fun TextView.setSpanViewMore(onClick: () -> Unit) {
       text = "$text${context.getString(R.string.str_see_more_1)}"
        movementMethod = LinkMovementMethod.getInstance()
        setText(addClickablePartTextViewResizable(Html.fromHtml(text.toString()), context.getString(R.string.str_see_more)) {
            onClick.invoke()
        }, TextView.BufferType.SPANNABLE)
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, spanableText: String, onclick: (() ->Unit)
    ): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        try {
            if (str.contains(spanableText)) {
                ssb.setSpan(object : MySpannable(false) {
                    override fun onClick(p0: View) {
                        onclick.invoke()
                    }
                }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
            }
        } catch (e: java.lang.IndexOutOfBoundsException) {
            LoggerUtil.e("addClickablePartTextViewResizable,2 error=${e.stackTraceToString()}")
        }
        return ssb
    }

    fun stopTextViewWriting() {
        binding.chatItemListLeftText.stopAnimateText()
        if (this.mData?.isSeeMore == true) {
            binding.chatItemListLeftText.setSpanViewMore {
                onClickViewMore?.invoke()
            }
        }
        if (this.mData?.isLastItem == true && this.mData?.isTyping == false) binding.btnRegeberate.visible()
    }
    fun updateText(text : String, index : Int){
        binding.chatItemListLeftText.updateIndex(index)
    }
    fun updateTextMore(text : String){
        binding.chatItemListLeftText.animateTextAdd(text)
    }

    fun setCharacterDelay(millis: Long) {
        binding.chatItemListLeftText.setCharacterDelay(millis)
    }

    private fun updateTypingAnimation(isRun: Boolean) {
        isTypingRunning = isRun
        if (isRun) {
            binding.chatItemListLeftAnimTypePing.visible()
            binding.chatItemListLeftAnimTypePing.playAnimation()
            binding.chatItemListLeftText.invisible()
            binding.chatItemListCopy.invisible()
            binding.btnRegeberate.invisible()
        } else {
            binding.chatItemListLeftAnimTypePing.cancelAnimation()
            binding.chatItemListLeftAnimTypePing.invisible()
            binding.chatItemListCopy.visible()
            binding.chatItemListLeftText.visible()
        }
    }
}