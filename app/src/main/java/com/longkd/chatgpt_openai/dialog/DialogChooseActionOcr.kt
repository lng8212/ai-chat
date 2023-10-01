
package com.longkd.chatgpt_openai.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.longkd.chatgpt_openai.databinding.DialogChooseActionImageBinding

class DialogChooseActionOcr : BaseDialog<DialogChooseActionImageBinding>() {
    var actionOpenCamera: (() -> Unit)? = null
    var actionSelectImage: (() -> Unit)? = null
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
        mBinding?.dialogActionBtnCancel?.setOnSingleClick {
            dismissAllowingStateLoss()
        }
        mBinding?.dialogActionBtnOpenCamera?.setOnSingleClick {
            actionOpenCamera?.invoke()
            dismissAllowingStateLoss()
        }
        mBinding?.dialogActionBtnSelectPhoto?.setOnSingleClick {
            actionSelectImage?.invoke()
            dismissAllowingStateLoss()
        }
    }

    override fun initBinding(): DialogChooseActionImageBinding {
        return DialogChooseActionImageBinding.inflate(LayoutInflater.from(context))
    }


    companion object {
        private const val TAG = "DialogChooseActionOcr"
        fun newInstance(): DialogChooseActionOcr {
            val dialog = DialogChooseActionOcr()
            return dialog
        }

        fun show(fm: FragmentManager): DialogChooseActionOcr {
            val fragment = newInstance()
            fragment.show(fm, TAG)
            return fragment
        }
    }
}