package com.longkd.chatgpt_openai.base.util

import android.util.Log


object LoggerUtil {

    private var mClassName: String = ""
    private var mMethodName: String = ""
    private var mLineNumber: Int = 0

    private fun getMethodNames(sElements: Array<StackTraceElement>) {
        kotlin.runCatching {
            mClassName = sElements.getOrNull(1)?.fileName.toString()
            mMethodName = sElements.getOrNull(1)?.methodName.toString()
            mLineNumber = sElements.getOrNull(1)?.lineNumber ?:0
        }
    }

    private fun createLog(log: String): String {
        val buffer = StringBuffer()
        buffer.append("[")
        buffer.append(mMethodName)
        buffer.append(":")
        buffer.append(mLineNumber)
        buffer.append("]")
        buffer.append(log)

        return buffer.toString()
    }
    @JvmStatic
    fun d(message: String) {
        getMethodNames(Throwable().stackTrace)
        Log.d("${Constants.TAG} - $mClassName", createLog(message))
    }

    @JvmStatic
    fun e(message: String) {
        getMethodNames(Throwable().stackTrace)
        Log.e("${Constants.TAG} - $mClassName", createLog(message))
    }
}