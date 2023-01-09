package com.side.domain.base

import com.google.gson.annotations.SerializedName

data class BaseResponse<out T>(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T,
)