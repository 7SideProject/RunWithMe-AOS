package com.side.data.model.response

data class ChallengeDetailResponse(
    val seq: Long,
    val canRunning: Boolean,
    val challengeUserFlag: Boolean,
    val cost: Int,
    val dateStart: String,
    val dateEnd: String,
    val description: String,
    val goalAmount: Int,
    val goalDays: Int,
    val goalType: String,
    val managerName: String,
    val managerSeq: Int,
    val maxMember: Int,
    val name: String,
    val nowMember: Int
)