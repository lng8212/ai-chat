package com.longkd.chatai.ui.main.chat.detail.adapter

import android.annotation.SuppressLint
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import com.longkd.base_android.base.BaseViewHolder
import com.longkd.base_android.ktx.invisible
import com.longkd.base_android.ktx.setOnSingleClick
import com.longkd.base_android.ktx.visible
import com.longkd.chatai.R
import com.longkd.chatai.data.ChatDetailDto
import com.longkd.chatai.databinding.ItemChatLeftBinding
import com.longkd.chatai.ui.custom.MySpannable

/**
 * @Author: longkd
 * @Since: 21:58 - 29/09/2023
 */
class ChatItemLeftViewHolder(
    binding: ItemChatLeftBinding, private var enableAnimateText: Boolean,
    private var mOnAnimateFinished: (() -> Unit)? = null,
    private var itemClickListener: (ChatDetailDto, position: Int) -> Unit,
    private var onClickRegenerate: ((chatDto: ChatDetailDto) -> Unit)? = null,
    private var onClickViewMore: (() -> Unit)? = null
) :
    BaseViewHolder<ChatDetailDto, ItemChatLeftBinding>(binding) {
    private var isTypingRunning = false
    private var mData: ChatDetailDto? = null
    override fun onBind(item: ChatDetailDto) {
        this.mData = item
        handleAnimateLeft()
        binding.chatItemListCopy.setOnSingleClick {
            itemClickListener.invoke(item, adapterPosition)
        }
        binding.btnRegenerate.invisible()
        binding.btnRegenerate.setOnSingleClick {
            onClickRegenerate?.invoke(item)
        }
        if (item.message != itemView.context.getString(R.string.text_title_start_chat)) {
            updateTypingAnimation(item.isTyping)
        } else {
            binding.chatItemListCopy.invisible()
            binding.btnRegenerate.invisible()
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
            binding.chatItemListLeftText.mOnAnimateFinished = {
                mOnAnimateFinished?.invoke()
                if (this.mData?.isSeeMore == true) {
                    binding.chatItemListLeftText.setSpanViewMore {
                        onClickViewMore?.invoke()
                    }
                }
                if (this.mData?.isLastItem == true && this.mData?.isTyping == false) {
                    binding.btnRegenerate.visible()
                } else {
                    binding.btnRegenerate.invisible()
                }
            }
        } else {
            binding.chatItemListLeftText.setTextNormal(this.mData?.message?.trim())
            binding.chatItemListLeftText.text = this.mData?.message?.trim()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun TextView.setSpanViewMore(onClick: () -> Unit) {
        text = "$text${context.getString(R.string.text_see_more_1)}"
        movementMethod = LinkMovementMethod.getInstance()
        setText(
            addClickablePartTextViewResizable(
                Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_LEGACY), context.getString(
                    R.string.text_see_more
                )
            ) {
                onClick.invoke()
            }, TextView.BufferType.SPANNABLE
        )
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, spannableText: String, onclick: (() -> Unit)
    ): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        try {
            if (str.contains(spannableText)) {
                ssb.setSpan(object : MySpannable(false) {
                    override fun onClick(p0: View) {
                        onclick.invoke()
                    }
                }, str.indexOf(spannableText), str.indexOf(spannableText) + spannableText.length, 0)
            }
        } catch (e: java.lang.IndexOutOfBoundsException) {
            Log.e("addClickablePartTextViewResizable,2", " error=${e.stackTraceToString()}")
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
        if (this.mData?.isLastItem == true && this.mData?.isTyping == false) binding.btnRegenerate.visible()
    }

    fun updateText(text: String, index: Int) {
        binding.chatItemListLeftText.updateIndex(index)
    }

    fun updateTextMore(text: String) {
        binding.chatItemListLeftText.animateTextAdd(text)
    }

    fun setCharacterDelay(millis: Long) {
        binding.chatItemListLeftText.setCharacterDelay(millis)
    }

    fun updateTypingAnimation(isRun: Boolean) {
        isTypingRunning = isRun
        if (isRun) {
            binding.chatItemListLeftAnimTypePing.visible()
            binding.chatItemListLeftAnimTypePing.playAnimation()
            binding.chatItemListLeftText.invisible()
            binding.chatItemListCopy.invisible()
            binding.btnRegenerate.invisible()
        } else {
            binding.chatItemListLeftAnimTypePing.cancelAnimation()
            binding.chatItemListLeftAnimTypePing.invisible()
            binding.chatItemListCopy.visible()
            binding.chatItemListLeftText.visible()
        }
    }

}
