package com.side.domain.model

data class User(
    val seq: Long,
    val email: String,
    val nickname: String,
    val height: Int,
    val weight: Int,
    val point: Int,
    val profileImgSeq: Long,
    val password: String? = null
)