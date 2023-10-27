package com.side.domain.utils

import java.io.IOException

sealed class ResultType<out T> {
    object Uninitialized : ResultType<Nothing>()

    object Loading : ResultType<Nothing>()

    object Empty : ResultType<Nothing>()

    object InputError : ResultType<Nothing>()

    data class Success<T>(val data: T) : ResultType<T>()

    data class Fail<T>(val data: T) : ResultType<T>()

    data class Error(val exception: Throwable) : ResultType<Nothing>() {
        val isNetworkError = exception is IOException
    }

    inline fun onSuccess(
        action: (value: T) -> Unit
    ): ResultType<T> {
        if(this is ResultType.Success) action(this.data)
        return this
    }

    inline fun onFailure(
        action: (value: T) -> Unit
    ): ResultType<T> {
        if(this is ResultType.Fail) action(this.data)
        return this
    }

    inline fun onError(
        action: (value: Throwable) -> Unit
    ) {
        if(this is ResultType.Error) action(this.exception)
        return
    }
}