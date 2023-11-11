package com.side.domain.model

data class RunningInfo(
    val challengeSeq: Long,
    val challengeName: String,
    val goalAmount: Long,
    val goalType: Int
)