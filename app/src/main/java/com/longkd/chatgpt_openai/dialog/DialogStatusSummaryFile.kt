package com.longkd.chatgpt_openai.dialog

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.longkd.chatgpt_openai.databinding.DialogStatusSummaryFileBinding

class DialogStatusSummaryFile: BaseDialog<DialogStatusSummaryFileBinding>() {

    var onClickGotIt: (() -> Unit) ? = null

    override fun initView() {
        val isShowIcon = arguments?.getBoolean(KEY_IS_SHOW_ICON)
        val title =  arguments?.getString(KEY_TITLE)
        mBinding?.mTitle = title
        mBinding?.isShowIcon = isShowIcon

        mBinding?.btnCancel?.setOnSingleClick {
            dismissAllowingStateLoss()
        }

        mBinding?.btnIGotIt?.setOnSingleClick {
            onClickGotIt?.invoke()
            dismissAllowingStateLoss()
        }
    }

    override fun initBinding(): DialogStatusSummaryFileBinding {
        return DialogStatusSummaryFileBinding.inflate(LayoutInflater.from(context))

    }
    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_IS_SHOW_ICON = "KEY_IS_SHOW_ICON"
        private const val TAG = "DialogStatusSummaryFile"
        fun newInstance(title: String, isShowIcon: Boolean): DialogStatusSummaryFile {
            val args = Bundle()
            args.putBoolean(KEY_IS_SHOW_ICON, isShowIcon)
            args.putString(KEY_TITLE, title)
            val fragment = DialogStatusSummaryFile()
            fragment.arguments = args
            return fragment
        }

        fun show(fm: FragmentManager, title: String, isShowIcon: Boolean): DialogStatusSummaryFile {
            val fragment = newInstance(title, isShowIcon)
            fragment.show(fm, TAG)
            return fragment
        }
    }
}