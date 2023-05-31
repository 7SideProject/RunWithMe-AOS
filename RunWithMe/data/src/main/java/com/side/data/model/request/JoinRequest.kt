package com.side.data.model.request

data class JoinRequest(
    val email: String,
    val password: String?,
    val nickname: String,
    val height: Int,
    val weight: Int
)
