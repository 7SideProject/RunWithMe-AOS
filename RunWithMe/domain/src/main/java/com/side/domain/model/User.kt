package com.side.domain.model

data class User(
    val seq: Long,
    val id: String,
    val nickname: String,
    val height: Int,
    val weight: Int,
    val point: Int,
    val profileImgSeq: Long,
    val password: String? = null
) {

    constructor(id: String, password: String) : this(
        seq = 0,
        id = id,
        nickname = "",
        height = 0,
        weight = 0,
        point = 0,
        profileImgSeq = 0,
        password = password
    )


}