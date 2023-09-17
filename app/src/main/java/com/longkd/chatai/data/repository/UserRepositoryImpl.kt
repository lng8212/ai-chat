package com.longkd.chatai.data.repository

import com.longkd.base_android.core.DataState
import com.longkd.base_android.data.api.handleResponse
import com.longkd.base_android.ktx.stateOn
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Author: longkd
 * @Since: 20:18 - 13/08/2023
 */

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userService: com.longkd.chatai.data.api.UserService,
    private val ioDispatcher: CoroutineDispatcher
) : com.longkd.chatai.domain.UserRepository {
    override suspend fun getAllData(page: Int): DataState<com.longkd.chatai.data.api.response.UserResponse, Exception> =
        stateOn(ioDispatcher) {
            val response = handleResponse(userService.getAllData(page))
            response.get()
        }

}