package com.side.domain.base

import com.google.gson.annotations.SerializedName

data class BaseResponse<out T>(
    val code: String,
    val message: String,
    val data: T,
)