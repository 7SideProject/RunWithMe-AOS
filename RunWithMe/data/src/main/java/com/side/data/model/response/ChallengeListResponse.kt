package com.side.data.model.response

import com.side.domain.model.Challenge

data class ChallengeListResponse(
    val seq : Long,
    val managerSeq: Long,
    val managerName: String,
    val name: String,
    val description: String,
    val image: Long,
    val goalDays: Int,
    val goalType: String,
    val goalAmount: Long,
    val dateStart : String,
    val dateEnd: String,
    val nowMember: Int,
    val maxMember: Int,
    val cost: Int,
    val passwordIsNull: Boolean
)
