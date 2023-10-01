package com.longkd.chatgpt_openai.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.bumptech.glide.Glide
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.databinding.DialogExitAppBinding


class DialogExitApp : BaseDialog<DialogExitAppBinding>() {
    private var action: (() -> Unit)? = null
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun initView() {
        mBinding?.dialogExitTvSubmit?.setOnClickListener {
            action?.invoke()
        }
        mBinding?.dialogExitTvCancel?.setOnClickListener {
            dismissAllowingStateLoss()
        }
        mBinding?.dialogExitTopImg?.let {
            Glide.with(this).load(R.drawable.img_dialog_exit).into(
                it
            )
        }
    }


    companion object {
        fun newInstance( actionExit: (() -> Unit)?): DialogExitApp {
            val dialog = DialogExitApp()
            dialog.action = actionExit
            return dialog
        }

    }

    override fun initBinding(): DialogExitAppBinding {
        return DialogExitAppBinding.inflate(LayoutInflater.from(context))
    }
}