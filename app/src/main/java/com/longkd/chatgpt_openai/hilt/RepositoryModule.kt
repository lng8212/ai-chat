/*
 * Created by longkd on 10/3/23, 10:30 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/3/23, 10:30 PM
 */

package com.longkd.chatgpt_openai.hilt

import com.longkd.chatgpt_openai.open.chat.ChatRepository
import com.longkd.chatgpt_openai.open.chat.ChatRepositoryImpl
import com.longkd.chatgpt_openai.open.image.ImageRepository
import com.longkd.chatgpt_openai.open.image.ImageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @Author: longkd
 * @Since: 22:30 - 03/10/2023
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    abstract fun bindImageRepository(imageRepositoryImpl: ImageRepositoryImpl): ImageRepository
}