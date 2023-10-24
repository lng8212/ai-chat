/*
 * Created by longkd on 10/3/23, 10:07 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/3/23, 10:07 PM
 */

package com.longkd.chatgpt_openai.hilt

import android.content.Context
import androidx.room.Room
import com.longkd.chatgpt_openai.BuildConfig
import com.longkd.chatgpt_openai.base.data.ChatDatabase
import com.longkd.chatgpt_openai.base.data.CoreDao
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.open.chat.ChatService
import com.longkd.chatgpt_openai.open.image.ImageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @Author: longkd
 * @Since: 22:07 - 03/10/2023
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationCompenent (i.e. everywhere in the application)
    @Provides
    fun provideChatDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context, ChatDatabase::class.java,
        "chat_ai_database.db"
    ).fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideChatDao(db: ChatDatabase) = db.demoDao()

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ChatService =
        retrofit.create(ChatService::class.java)

    @Singleton
    @Provides
    fun provideImageService(retrofit: Retrofit): ImageService =
        retrofit.create(ImageService::class.java)

    @Singleton
    @Provides
    fun provideDataRepository(dao: CoreDao) = DataRepository(dao)
}