package com.side.data.model.response

data class KaKaoLoginResponse(
    val id: Long,
    val accessToken: String,
    val refreshToken: String,
    val isInitialized: Boolean
)
