package com.side.data.model.response

import java.util.*

data class ChallengeResponse(
    val seq : Long,
    val managerSeq: Long,
    val name: String,
    val description: String,
    val imgSeq: Long,
    val goalDays: Int,
    val goalType: String,
    val goalAmount: Long,
    val dateStart : String,
    val dateEnd: String,
    val maxMember: Int,
    val cost: Int,
    val password: String? = null
)