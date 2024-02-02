package com.side.data.model.response

data class MyTotalRecordInChallengeResponse(
    val totalTime: Int,
    val totalDistance: Int,
    val totalCalorie: Int,
    val totalLongestTime: Int,
    val totalLongestDistance: Int,
    val totalAvgSpeed: Double
)