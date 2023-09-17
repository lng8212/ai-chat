package com.longkd.base_android.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.longkd.base_android.R
import com.longkd.base_android.databinding.DialogCustomBinding

/**
 * @Author: longkd
 * @Date: 10:07 AM - 8/24/2023
 */
object DialogUtils {
    fun showCustomDialog(activity: Activity, onLaunchListener: () -> Unit) {
        val dialog = Dialog(activity, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogCustomBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        dialog.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        binding.run {
            btnDone.setOnClickListener {
                onLaunchListener.invoke()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}