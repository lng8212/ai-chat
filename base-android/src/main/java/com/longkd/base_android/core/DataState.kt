package com.longkd.base_android.core

/**
 * @Author: longkd
 * @Since: 22:38 - 11/08/2023
 */

sealed class DataState<out D, out E : Exception> {

    object Idle : DataState<Nothing, Nothing>()
    object Loading : DataState<Nothing, Nothing>()
    data class Success<out D>(val data: D) : DataState<D, Nothing>()
    data class Error<out E : Exception>(val error: E) : DataState<Nothing, E>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    fun get() = requireData()

    fun getOrNull() = getDataOrNull()

    fun getDataOrNull(): D? {
        return if (this is Success) this.data
        else null
    }

    fun requireData(): D {
        check(isSuccess) { "${this::class.java.name} is not a Success" }
        return (this as Success).data
    }

    fun error() = requiredError()

    fun getErrorOrNull(): E? {
        return if (this is Error) this.error
        else null
    }

    fun requiredError(): E {
        check(isError) { "${this::class.java.name} is not a Error" }
        return (this as Error).error
    }
}
