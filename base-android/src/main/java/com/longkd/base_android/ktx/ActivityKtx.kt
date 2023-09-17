package com.longkd.base_android.ktx

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity

/**
 * @Author: longkd
 * @Since: 10:16 - 12/08/2023
 */
fun FragmentActivity.hideKeyBoard() {
    currentFocus?.let {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}