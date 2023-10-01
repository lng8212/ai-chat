package com.longkd.chatgpt_openai.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseBottomSheetDialogFragment
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.databinding.DialogBottomExitAppBinding

class DialogBottomExitApp : BaseBottomSheetDialogFragment<DialogBottomExitAppBinding>() {
    var onClickExit: (() -> Unit)? = null

    override val layoutId: Int
        get() = R.layout.dialog_bottom_exit_app

    override val backgroundTransparent: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.dialogExitBtnExit.setOnSingleClick {
            onClickExit?.invoke()
            dismissAllowingStateLoss()
        }
    }

    companion object {
        fun newInstance(): DialogBottomExitApp {
            val args = Bundle()
            val fragment = DialogBottomExitApp()
            fragment.arguments = args
            return fragment
        }

        fun dismiss(fm: FragmentManager) {
            val fragment = fm.findFragmentByTag(DialogBottomExitApp::class.simpleName)
            if (fragment is DialogBottomExitApp) {
                fragment.dismiss()
            }
        }

        fun show(fm: FragmentManager): DialogBottomExitApp {
            val fragment = newInstance()
            fragment.show(fm, DialogBottomExitApp::class.simpleName)
            return fragment
        }
    }
}