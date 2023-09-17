package com.longkd.base_android.data.api

import com.google.gson.GsonBuilder
import com.longkd.base_android.annotation.ApiService
import com.longkd.base_android.annotation.ConfigAuthentication
import com.longkd.base_android.annotation.EnableAuthentication
import com.longkd.base_android.annotation.Level
import com.longkd.base_android.annotation.LoggingLever
import com.longkd.base_android.annotation.Timeout
import com.longkd.base_android.ktx.registerValueChangeListener
import com.longkd.base_android.utils.AppSharedPreference
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Author: longkd
 * @Since: 12:51 - 13/08/2023
 */
class ApiServiceBuilder<SERVICE>(private val serviceClass: Class<SERVICE>) {
    var baseUrl: String = ""
    var converterFactory: Converter.Factory? = null
    val interceptors: MutableList<Interceptor> = mutableListOf()
    var readTimeout: Long = 10_000 //  10s
    var writeTimeout: Long = 10_000 // 10s
    var loggingLever: Level = Level.NONE
    private var token: String = ""
    private var tokenType: String = ""
    private var tokenKey: String? = null

    fun baseurl(baseUrl: String): ApiServiceBuilder<SERVICE> {
        this.baseUrl = baseUrl
        return this
    }

    fun converterFactory(converter: Converter.Factory): ApiServiceBuilder<SERVICE> {
        this.converterFactory = converter
        return this
    }

    fun readTimeout(timeout: Long): ApiServiceBuilder<SERVICE> {
        this.readTimeout = timeout
        return this
    }

    fun writeTimeout(timeout: Long): ApiServiceBuilder<SERVICE> {
        this.writeTimeout = timeout
        return this
    }

    fun addInterceptor(interceptor: Interceptor): ApiServiceBuilder<SERVICE> {
        this.interceptors.add(interceptor)
        return this
    }

    fun interceptors(interceptors: List<Interceptor>): ApiServiceBuilder<SERVICE> {
        this.interceptors.addAll(interceptors)
        return this
    }

    fun loggingLever(lever: Level): ApiServiceBuilder<SERVICE> {
        this.loggingLever = lever
        return this
    }

    private fun buildClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
        interceptors.forEach { builder.addInterceptor(it) }
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.valueOf(loggingLever.name)
        builder.addNetworkInterceptor(logging)
        return builder.build()
    }

    private fun addAuthenticationInterceptor(headerName: String) {
        val authInterceptor = if (tokenKey != null) {
            Interceptor { chain ->
                token =
                    AppSharedPreference.get().getString(tokenKey!!, "")!!
                val fullToken = "$tokenType $token"
                val authRequest =
                    chain.request().newBuilder().addHeader(headerName, fullToken).build()
                chain.proceed(authRequest)
            }
        } else {
            Interceptor { chain ->
                val fullToken = token
                val authRequest =
                    chain.request().newBuilder().addHeader(headerName, fullToken).build()
                chain.proceed(authRequest)
            }
        }
        interceptors.add(authInterceptor)
    }

    private fun registerAutoUpdateToken(key: String) {
        AppSharedPreference.get()
            .registerValueChangeListener<String>(key) {
                token = it
            }
    }

    private fun defaultConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create(
            GsonBuilder()
                .registerTypeAdapterFactory(GsonSerializeNullAdapterFactory())
                .create()
        )
    }

    fun getConfigFromAnnotation(): ApiServiceBuilder<SERVICE> {
        serviceClass.annotations.forEach { annotation ->
            when (annotation) {
                is ApiService -> {
                    baseUrl = annotation.baseUrl
                }

                is Timeout -> {
                    readTimeout = annotation.read
                    writeTimeout = annotation.write
                }

                is LoggingLever -> {
                    loggingLever = annotation.level
                }

                is EnableAuthentication -> {
                    tokenType = annotation.type
                    /**
                     * isAutoUpdate == true: auto update [token] from SharedPreference without getting for each request
                     * isAutoUpdate == false: for each request get [token] from SharedPreference by [tokenKey]
                     */
                    if (annotation.isAutoUpdate) {
                        token = AppSharedPreference.get()
                            .getString(annotation.key, "")!!
                        registerAutoUpdateToken(annotation.key)
                    } else {
                        tokenKey = annotation.key
                    }
                    addAuthenticationInterceptor(annotation.headerName)
                }

                is ConfigAuthentication -> {
                    token = annotation.token
                    tokenType = annotation.type
                    addAuthenticationInterceptor(annotation.headerName)
                }
            }
        }
        return this
    }

    fun build(): SERVICE {
        require(baseUrl.isNotEmpty()) {
            "Base url must be config"
        }
        /**
         * Add `/` if needed at the end of base url
         */
        if (!baseUrl.endsWith('/')) baseUrl = "${baseUrl}/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(buildClient())
            .addConverterFactory(converterFactory ?: defaultConverterFactory())
            .build()
            .create(serviceClass)
    }

}