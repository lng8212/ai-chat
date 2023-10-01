package com.longkd.chatgpt_openai.open.client

import com.longkd.chatgpt_openai.base.OpenAIHolder
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import okhttp3.*
import java.io.IOException

/**
 * OkHttp Interceptor that adds an authorization token header
 */
class AuthenticationInterceptor internal constructor(private val token: String, private val type:Int) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            OpenAIHolder.getResponseOld(
                chain,
                token,
                OpenAiUtils.userAgent,
                CommonSharedPreferences.getInstance().timeStamp,
                type
            )
        } catch (e: Throwable) {
            val request = chain.request()
            request.newBuilder()
                .header("User-Agent", OpenAiUtils.userAgent)
                .header("Authorization", "Bearer $token").build()
            chain.proceed(request)
        }
    }
}