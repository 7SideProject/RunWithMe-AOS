package com.side.data.model.response

data class ChallengeBoardsResponse(
    val boardSeq: Long,
    val userSeq: Long,
    val nickname: String,
    val content: String,
    val image: Boolean
)
