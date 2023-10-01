package com.longkd.chatgpt_openai.base.crashreport

import android.content.Context
import com.longkd.chatgpt_openai.R
import org.acra.data.CrashReportData
import org.acra.sender.ReportSender

class ExReportSender : ReportSender {

    override fun send(context: Context, errorContent: CrashReportData) {
        ExErrorActivity.reportError(
            context, errorContent,
            ExErrorActivity.ErrorInfo.make(
                ExErrorActivity.ERROR_UI_ERROR,
                "Application crash", R.string.app_ui_crash
            )
        )
    }

    override fun requiresForeground(): Boolean {
        return true
    }
}
