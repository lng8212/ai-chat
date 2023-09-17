package com.longkd.chatai.domain

import com.longkd.base_android.core.DataState

/**
 * @Author: longkd
 * @Since: 20:14 - 13/08/2023
 */
interface UserRepository {
    suspend fun getAllData(page: Int): DataState<com.longkd.chatai.data.api.response.UserResponse, Exception>
}