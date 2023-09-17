package com.longkd.chatai.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @Author: longkd
 * @Since: 20:28 - 13/08/2023
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(authRepositoryImpl: com.longkd.chatai.data.repository.UserRepositoryImpl): com.longkd.chatai.domain.UserRepository
}