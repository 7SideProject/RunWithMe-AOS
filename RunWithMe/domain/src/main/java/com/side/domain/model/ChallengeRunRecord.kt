package com.side.domain.model

data class ChallengeRunRecord(
    val seq: Long,
    val userSeq: Long,
    val nickname: String,
    val runningDay: String,
    val startTime: String,
    val runningDistance: Int,
    val runningTime: Int,
    val calorie: Int,
    val avgSpeed: Double
)
