package com.longkd.chatai.di

import android.content.Context
import com.longkd.base_android.data.CommonSharedPreference
import com.longkd.base_android.data.api.ApiService
import com.longkd.base_android.utils.AppSharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * @Author: longkd
 * @Since: 22:54 - 12/08/2023
 */

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideAppSharePreference(@ApplicationContext context: Context): CommonSharedPreference {
        AppSharedPreference.create(context)
        return AppSharedPreference.get()
    }

    @Singleton
    @Provides
    fun provideUserService(): com.longkd.chatai.data.api.UserService = ApiService()

    @Singleton
    @Provides
    fun provideIODispatcher() = Dispatchers.IO
}