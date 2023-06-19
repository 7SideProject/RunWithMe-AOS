package com.side.data.model.response

import java.util.*

data class ChallengeResponse(
    val seq : Long,
    val managerSeq: Long,
    val name: String,
    val imgSeq: Long,
    val goalDays: Long,
    val goalType: String,
    val goalAmount: Long,
    val timeStart : Date,
    val timeEnd: Date
)