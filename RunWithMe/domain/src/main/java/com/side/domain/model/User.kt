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
) {

    constructor(email: String, password: String) : this(
        seq = 0,
        email = email,
        nickname = "",
        height = 0,
        weight = 0,
        point = 0,
        profileImgSeq = 0,
        password = password
    )


}