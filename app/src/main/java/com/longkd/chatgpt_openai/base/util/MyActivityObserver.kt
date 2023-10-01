package com.longkd.chatgpt_openai.base.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MyActivityObserver(
    private val onStart: () -> Unit,
    private val onPause: () -> Unit
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        onStart.invoke()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        onPause.invoke()
    }

}