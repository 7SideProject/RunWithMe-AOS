package com.side.data.model.response

data class ChallengeRecordsResponse(
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