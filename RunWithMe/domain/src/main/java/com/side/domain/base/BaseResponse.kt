package com.side.domain.base

data class BaseResponse<out T>(
    val code: Int,
    val message: String,
    val data: T,
)

fun <T, C> BaseResponse<C>.changeData(changeData: T): BaseResponse<T> =
    BaseResponse(
        this.code,
        this.message,
        changeData
    )
