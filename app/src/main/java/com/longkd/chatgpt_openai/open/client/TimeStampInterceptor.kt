package com.longkd.chatgpt_openai.open.client

import com.longkd.chatgpt_openai.base.OpenAIHolder
import okhttp3.*
import java.io.IOException

/**
 * OkHttp Interceptor that adds an authorization token header
 */
class TimeStampInterceptor internal constructor(private val token: String, private val type:Int) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            OpenAIHolder.getTimeStampResponse(chain, OpenAiUtils.userAgent)
        } catch (e: Throwable) {
            val request = chain.request()
            request.newBuilder()
                .header("User-Agent", OpenAiUtils.userAgent)
                .header("Authorization", "Bearer $token").build()
            chain.proceed(request)

        }
    }
}