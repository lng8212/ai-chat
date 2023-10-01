package com.longkd.chatgpt_openai.dialog

import android.view.LayoutInflater
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.longkd.chatgpt_openai.databinding.DialogSuggestWidgetBinding

class DialogSuggestWidget : BaseDialog<DialogSuggestWidgetBinding>() {
    private var actionOk: (() -> Unit)? = null

    companion object {

        fun newInstance(
            actionOk: (() -> Unit)?
        ): DialogSuggestWidget {
            val mDialog = DialogSuggestWidget()
            mDialog.actionOk = actionOk
            return mDialog
        }
    }

    override fun initView() {
        CommonSharedPreferences.getInstance().setShowDialogWidget(true)
        mBinding?.dlSuggestWidgetCLose?.setOnSingleClick {
            dismissAllowingStateLoss()
        }
        mBinding?.dlSuggestLlAdd?.setOnSingleClick {
            actionOk?.invoke()
            dismissAllowingStateLoss()
        }
    }

    override fun initBinding(): DialogSuggestWidgetBinding {
        return DialogSuggestWidgetBinding.inflate(LayoutInflater.from(context))
    }

}