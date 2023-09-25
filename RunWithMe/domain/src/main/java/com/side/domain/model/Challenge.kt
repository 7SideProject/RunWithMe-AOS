package com.side.domain.model

import java.util.Date

data class Challenge(
    val seq : Long,
    val managerSeq: Long,
    val name: String,
    val description: String,
    val imgSeq: Long,
    val goalDays: Int,
    val goalType: String,
    val goalAmount: Long,
    val timeStart : String,
    val timeEnd: String,
    val maxMember: Int,
    val cost: Int,
    val password: String? = null
)
