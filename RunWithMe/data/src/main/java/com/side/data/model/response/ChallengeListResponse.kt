package com.side.data.model.response

import com.side.domain.model.Challenge

data class ChallengeListResponse(
    val page: Int,
    val totalPage: Int,
    val result: List<Challenge>
)
