package com.side.data.model.response

data class UserResponse(
    val seq: Long,
    val email: String,
    val nickname: String,
    val height: Int,
    val weight: Int,
    val point: Int
)