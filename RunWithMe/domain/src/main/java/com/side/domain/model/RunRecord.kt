package com.side.domain.model

data class RunRecord(
    val runRecordSeq: Long,
    val startTime: String,
    val endTime: String,
    val runningDay: String,
    val runningTime: Int,
    val runningDistance: Int,
    val avgSpeed: Double,
    val calorie: Int,
    val successYN: String,
    val coordinates: List<Coordinate> = listOf()
)
