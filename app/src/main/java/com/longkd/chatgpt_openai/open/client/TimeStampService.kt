
package com.longkd.chatgpt_openai.open.client

import com.longkd.chatgpt_openai.open.dto.completion.*
import com.longkd.chatgpt_openai.open.dto.moderation.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TimeStampService {
    val api: OpenAiApi
    val mToken = ""

    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token   OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout in seconds, 0 means no timeout
     */
    @Deprecated("use {@link OpenAiService(String, Duration)}")
    constructor(token: String, timeout: Long,
                type:Int) : this(
        token,
        BASE_URL,
        timeout,
        type
    )

    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token   OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout, Duration.ZERO means no timeout
     */
    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token   OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout, Duration.ZERO means no timeout
     */
    /**
     * Creates a new OpenAiService that wraps OpenAiApi
     *
     * @param token OpenAi token string "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     */
    constructor(
        token: String,
        baseUrl: String = BASE_URL,
        timeout: Long = 10,
        type:Int
    ) {
//        val mapper = ObjectMapper()
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
//        mapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 1, TimeUnit.SECONDS))
            .readTimeout(timeout, TimeUnit.SECONDS)
            .connectTimeout(timeout * 2, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        api = retrofit.create(OpenAiApi::class.java)
    }

    fun getTimeStamp(): TokenDto {
        return api.getTime().blockingGet()
    }
    companion object {
        private const val BASE_URL = "http://54.179.51.65:80/"
    }
}