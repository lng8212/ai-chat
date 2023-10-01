package com.longkd.chatgpt_openai.base.util.threadexecutor

import android.util.Log
import java.util.concurrent.ThreadFactory
import android.os.Process
class BackgroundThreadFactory : ThreadFactory {

    override fun newThread(runnable: Runnable): Thread {
        val thread = Thread(runnable)
        thread.name = "CustomThread$sTag"
        thread.priority = Process.THREAD_PRIORITY_BACKGROUND

        // A exception handler is created to log the exception from threads
        thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, ex ->
            Log.e(
                "error",
                thread.name + " encountered an error: " + ex.message
            )
        }
        return thread
    }

    companion object {
        private val sTag = 1
    }
}