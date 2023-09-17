package com.longkd.chatai.ui

import com.longkd.base_android.base.BaseDialogFragment
import com.longkd.chatai.databinding.DialogTestBinding

/**
 * @Author: longkd
 * @Date: 9:44 AM - 8/24/2023
 */
class DialogTest : BaseDialogFragment<DialogTestBinding>() {
    var onClickListener: (() -> Unit)? = null

    companion object {
        fun newInstance(onClickListener: () -> Unit): com.longkd.chatai.ui.DialogTest {
            val dialogTest = com.longkd.chatai.ui.DialogTest()
            dialogTest.onClickListener = onClickListener
            return dialogTest
        }
    }

    override fun initView() {
        binding.btnDone.setOnClickListener {
            onClickListener?.invoke()
            dismiss()
        }
    }
}