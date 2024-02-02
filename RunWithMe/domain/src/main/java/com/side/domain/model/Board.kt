package com.side.domain.model

data class Board(
    val boardSeq: Long,
    val userSeq: Long,
    val nickname: String,
    val content: String,
    val image: Boolean
)