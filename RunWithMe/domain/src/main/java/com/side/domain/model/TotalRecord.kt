package com.side.domain.model

data class TotalRecord(
    val totalTime: Int,
    val totalDistance: Int,
    val totalCalorie: Int,
    val totalLongestTime: Int,
    val totalLongestDistance: Int,
    val totalAvgSpeed: Double
){
    constructor() : this(
        totalTime = 0,
        totalDistance = 0,
        totalCalorie = 0,
        totalLongestTime = 0,
        totalLongestDistance = 0,
        totalAvgSpeed = 0.0
    )
}
