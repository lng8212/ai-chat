

package com.longkd.chatgpt_openai.base

import android.content.SharedPreferences
import com.longkd.chatgpt_openai.base.model.SummaryFileResponse
import com.longkd.chatgpt_openai.open.client.OpenAiService
import com.longkd.chatgpt_openai.open.client.TimeStampService
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Result
import com.longkd.chatgpt_openai.open.dto.completion.CompletionRequest
import com.longkd.chatgpt_openai.open.dto.completion.CompletionResult
import com.longkd.chatgpt_openai.open.dto.completion.TokenDto


object OpenAIHolder {

    init {
        kotlin.runCatching {
            System.loadLibrary("openai")
        }
    }

    external fun initLib(): Boolean


    external fun callCompletion35(
        type: Int,
        message: String,
        service: OpenAiService,
        request: Completion35Request,
        sharedPreferences: SharedPreferences,
    ): Completion35Result?

    external fun completeSummaryChat(
        type: Int,
        message: String,
        service: OpenAiService,
        request: Completion35Request,
        sharedPreferences: SharedPreferences,
    ): Completion35Result?

    external fun callCompletionMore(
        type: Int,
        message: String,
        service: OpenAiService,
        request: CompletionRequest
    ): CompletionResult?

    external fun callGetTime(
        service: TimeStampService,
    ): TokenDto?

    external fun uploadSummaryText(
        type: Int,
        service: OpenAiService,
        request: Completion35Request,
        sharedPreferences: SharedPreferences,
    ): SummaryFileResponse?




}