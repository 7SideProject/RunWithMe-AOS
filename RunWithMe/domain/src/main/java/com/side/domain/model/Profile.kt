package com.side.domain.model

data class Profile(
    val nickname: String,
    val height: Int,
    val weight: Int
){
    constructor() : this(
        nickname = "",
        height = 0,
        weight = 0,
    )
}
