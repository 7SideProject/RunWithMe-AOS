package com.side.domain.model

import java.util.Date

data class Challenge(
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
