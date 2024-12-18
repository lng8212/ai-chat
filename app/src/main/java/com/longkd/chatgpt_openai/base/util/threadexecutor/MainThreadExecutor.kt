package com.longkd.chatgpt_openai.base.util.threadexecutor

import android.os.Handler
import android.os.Looper

import java.util.concurrent.Executor

class MainThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())

    override fun execute(runnable: Runnable) {
        handler.post(runnable)
    }

}
