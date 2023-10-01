package com.longkd.chatgpt_openai.base.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


fun <T : ViewDataBinding?> ViewGroup.bindingInflate(@LayoutRes resourceId: Int): T = DataBindingUtil.inflate<T>(
    LayoutInflater.from(context), resourceId, this, false
)