package com.longkd.chatgpt_openai.dialog

import android.os.Bundle
import android.view.LayoutInflater
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.longkd.chatgpt_openai.databinding.DialogFailArtBinding

class DialogFailArt : BaseDialog<DialogFailArtBinding>() {
    companion object {
        fun newInstance(): DialogFailArt {
            val args = Bundle()
            val fragment = DialogFailArt()
            fragment.arguments = args
            return fragment
        }
    }
    override fun initView() {
        mBinding?.dlFailArtTvSubmit?.setOnSingleClick {
            dismissAllowingStateLoss()
        }
    }

    override fun initBinding(): DialogFailArtBinding = DialogFailArtBinding.inflate(LayoutInflater.from(context))
}