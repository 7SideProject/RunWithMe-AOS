package com.side.data.model.response

data class ChallengeResponse(
    val seq: Long,
    val name: String,
    val founder: String,
    val duration: String,
    val method: String,
    val often: String,
    val distance: String,
    val totalMemberCount: Int,
    val memberCount: Int,
    val cost: Int
)