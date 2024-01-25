package com.side.domain.model

data class Board(
    val boardSeq: Long,
    val userSeq: Long,
    val nickname: String,
    val imgSeq: Long,
    val content: String
)