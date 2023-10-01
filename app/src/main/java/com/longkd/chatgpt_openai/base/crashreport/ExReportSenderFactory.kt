package com.longkd.chatgpt_openai.base.crashreport

import android.content.Context
import com.google.auto.service.AutoService
import org.acra.config.CoreConfiguration
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderFactory

@AutoService(ReportSenderFactory::class)
class ExReportSenderFactory : ReportSenderFactory {
    override fun create(context: Context, config: CoreConfiguration): ReportSender {
        return ExReportSender()
    }

    override fun enabled(config: CoreConfiguration): Boolean {
        return true
    }
}
