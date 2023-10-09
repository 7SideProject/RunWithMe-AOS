package com.side.domain.model


data class Challenge(
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
