package com.longkd.chatgpt_openai.dialog

import android.view.LayoutInflater
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.longkd.chatgpt_openai.databinding.DialogConfirmDeleteChatBinding


class DialogConfirmDeleteChat : BaseDialog<DialogConfirmDeleteChatBinding>() {
private var mActionConfirm:(()->Unit)?=null

    override fun initView() {
        mBinding?.dialogConfrirmDeleteTvSubmit?.setOnClickListener {
            mActionConfirm?.invoke()
            dismissAllowingStateLoss()
        }
        mBinding?.dialogConfrirmDeleteTvCancel?.setOnClickListener {
            dismissAllowingStateLoss()
        }

    }


    companion object {
        fun newInstance(actionConfirm:(()->Unit)?=null): DialogConfirmDeleteChat {
            val dialog = DialogConfirmDeleteChat()
            dialog.mActionConfirm = actionConfirm
            return dialog
        }

    }

    override fun initBinding(): DialogConfirmDeleteChatBinding {
        return DialogConfirmDeleteChatBinding.inflate(LayoutInflater.from(context))
    }
}