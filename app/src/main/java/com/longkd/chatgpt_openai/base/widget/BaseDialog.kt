package com.longkd.chatgpt_openai.base.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.longkd.chatgpt_openai.base.util.LoggerUtil
import com.longkd.chatgpt_openai.base.util.Strings

abstract class BaseDialog<VDB : ViewDataBinding> : DialogFragment() {

    var mOnDismiss = false
        private set
    var mBinding: VDB? = null
        private set

    var onDialogDismissListener: (() -> Unit)? = null

    override fun show(manager: FragmentManager, tag: String?) {
        val t = javaClass.name
        val exitDialog = manager.findFragmentByTag(t)
        if (activity?.isDestroyed == true || activity?.isFinishing == true)
            return
        if (exitDialog == null) {
            try {
                val ft: FragmentTransaction = manager.beginTransaction()
                ft.add(this, t)
                ft.commitAllowingStateLoss()
            } catch (e: IllegalStateException) {
                LoggerUtil.d("show: ${e.message}")
            }
        }
    }

    fun show(manager: FragmentManager?) {
        manager?.let {
            show(it, null)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(
            requireContext()
        )
        mBinding = initBinding()
        dialogBuilder.setView(mBinding?.root)
        initView()
        return dialogBuilder.create()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val windowParams = window?.attributes
           // windowParams?.windowAnimations = R.style.BottomDialog
//            windowParams?.dimAmount = 0.7f
//            windowParams?.flags =
//                windowParams?.flags?.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//            windowParams?.flags =
//                windowParams?.flags?.or(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //            windowParams.windowAnimations = R.style.DialogAnimation;
            window?.attributes = windowParams
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            //            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        mOnDismiss = true
        hideKeyboard(null)
        onDialogDismissListener?.invoke()
        super.onDismiss(dialog)
    }

    private fun hideKeyboard(edt: EditText?) {
        val imm: InputMethodManager? = edt?.context
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        if (imm?.isActive == true) imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun getStringRes(@StringRes res: Int): String {
        return try {
            context?.resources?.getString(res) ?: Strings.EMPTY
        } catch (e: Exception) {
            Strings.EMPTY
        }
    }

    fun closeDialog(){
        try {
            dismiss()
        }catch (e:Exception){
            dismissAllowingStateLoss()
        }
    }

    abstract fun initView()
    abstract fun initBinding(): VDB

}