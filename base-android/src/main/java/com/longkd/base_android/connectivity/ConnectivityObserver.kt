package com.longkd.base_android.connectivity

import kotlinx.coroutines.flow.Flow

/**
 * @Author: longkd
 * @Since: 10:45 - 12/08/2023
 */
interface ConnectivityObserver {

    fun observer(): Flow<Status>

    enum class Status {
        AVAILABLE, UNAVAILABLE, LOSING, LOST;

        fun hasNetwork(): Boolean = this == AVAILABLE
    }

}