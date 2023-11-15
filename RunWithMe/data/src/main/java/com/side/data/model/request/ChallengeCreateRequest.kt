package com.side.data.model.request

data class ChallengeCreateRequest(
    val name: String,
    val description: String,
    val goalDays: Int,
    val goalType: String,
    val goalAmount: Long,
    val dateStart: String,
    val dateEnd: String,
    val cost: Int,
    val maxMember: Int,
    val password: String? = null
)
