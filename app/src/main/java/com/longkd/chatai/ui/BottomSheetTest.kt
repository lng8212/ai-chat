package com.longkd.chatai.ui

import android.content.DialogInterface
import com.longkd.base_android.base.BaseBottomSheetFragment
import com.longkd.chatai.databinding.BottomSheetTestBinding

/**
 * @Author: longkd
 * @Date: 10:17 AM - 8/24/2023
 */
class BottomSheetTest : BaseBottomSheetFragment<BottomSheetTestBinding>() {

    var onClickListener: (() -> Unit)? = null

    companion object {
        fun newInstance(onClickListener: () -> Unit): com.longkd.chatai.ui.BottomSheetTest {
            val bottomSheetTest = com.longkd.chatai.ui.BottomSheetTest()
            bottomSheetTest.onClickListener = onClickListener
            return bottomSheetTest
        }
    }

    override fun initView() {

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onClickListener?.invoke()
    }
}