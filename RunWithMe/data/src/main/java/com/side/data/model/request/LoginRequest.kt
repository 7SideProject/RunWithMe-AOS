package com.side.data.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("code") val code: String,
    @SerializedName("state") val state: String
)