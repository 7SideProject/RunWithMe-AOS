package com.side.data.model.request

data class CreateChallengeRequest(
    val name: String,
    val description: String,
    val goalDays: Int,
    val goalType: String,
    val goalAmount: Long,
    val timeStart: String,
    val timeEnd: String,
    val password: String?,
    val cost: Int,
    val maxMember: Int
)