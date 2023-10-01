

package com.longkd.chatgpt_openai.base

import android.content.SharedPreferences
import com.longkd.chatgpt_openai.open.client.OpenAiService
import com.longkd.chatgpt_openai.open.client.TimeStampService
import com.longkd.chatgpt_openai.open.dto.completion.*
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtRequest
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtResult
import com.longkd.chatgpt_openai.open.dto.image_art.ImageArtRequest
import com.longkd.chatgpt_openai.open.dto.image_art.ImageArtResult
import com.longkd.chatgpt_openai.base.model.GenerateArtByVyroRequest
import com.longkd.chatgpt_openai.base.model.GenerateArtResponse
import com.longkd.chatgpt_openai.base.model.ImageStyleResponse
import com.longkd.chatgpt_openai.base.model.SummaryFileResponse
import com.longkd.chatgpt_openai.base.model.TopicResponse
import okhttp3.Interceptor
import okhttp3.Response


object OpenAIHolder {

    init {
        kotlin.runCatching {
            System.loadLibrary("openai")
        }
    }

    external fun initLib(): Boolean

    external fun getResponse(chain: Interceptor.Chain, token: String, type: Int): Response

    external fun getChatFreeMessage(type: Int, sharedPreferences: SharedPreferences): Int
    external fun getChatFreeMessagePremium(type: Int, sharedPreferences: SharedPreferences): Int
    external fun verifyRewarded(type: Int, numberReward: Int,  sharedPreferences: SharedPreferences)
    external fun resetFreeChat( sharedPreferences: SharedPreferences)
    external fun putNumberGenerate( sharedPreferences: SharedPreferences, numberGenerate: Int)

    external fun callCompletion(
        type: Int,
        message: String,
        service: OpenAiService,
        request: CompletionRequest,
        sharedPreferences: SharedPreferences,
        vipSharedPreference: SharedPreferences
    ): CompletionResult?
    external fun callCompletionOld(
        type: Int,
        message: String,
        service: OpenAiService,
        request: CompletionRequest,
        sharedPreferences: SharedPreferences,
    ): CompletionResult?
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

    external fun completeTopicChat(
        type: Int,
        service: OpenAiService,
        request: Completion35Request,
        sharedPreferences: SharedPreferences,
    ): TopicResponse?

    external fun callCompletionMore(
        type: Int,
        message: String,
        service: OpenAiService,
        request: CompletionRequest
    ): CompletionResult?

    external fun callGetTime(
        service: TimeStampService,
    ): TokenDto?
    external fun decreaseNumberFreeChat(
        type: Int,
        sharedPreferences: SharedPreferences,
        vipSharedPreference: SharedPreferences
    )
    external fun decreaseNumberGenerate(
        sharedPreferences: SharedPreferences,
        vipSharedPreference: SharedPreferences
    )

    external fun getResponseOld(chain: Interceptor.Chain, token: String, userAgent : String, timeStamp : String, type: Int): Response


    external fun getTimeStampResponse(chain: Interceptor.Chain, userAgent : String): Response


    external fun generateAiArt(
        prompt: String,
        service: OpenAiService,
        request: GenerateArtRequest,
        sharedPreferences: SharedPreferences,
    ): GenerateArtResult?

    external fun uploadSummaryFile(
        service: OpenAiService,
        request: String,
        sharedPreferences: SharedPreferences,
    ): SummaryFileResponse?

    external fun uploadSummaryText(
        type: Int,
        service: OpenAiService,
        request: Completion35Request,
        sharedPreferences: SharedPreferences,
    ): SummaryFileResponse?

    external fun getImageAiArt(
        service: OpenAiService,
        request: ImageArtRequest,
        sharedPreferences: SharedPreferences
    ): ImageArtResult?

    external fun getKey(
        prompt: String
    ): String?
    external fun getFreeNumberGenerate(sharedPreferences: SharedPreferences): Int

    external fun getImageStyle(
        service: OpenAiService,
        folder: String,
    ): ImageStyleResponse?

    external fun generateArtByVyro(
        service: OpenAiService,
        request: GenerateArtByVyroRequest,
        sharedPreferences: SharedPreferences
    ): GenerateArtResponse?

}