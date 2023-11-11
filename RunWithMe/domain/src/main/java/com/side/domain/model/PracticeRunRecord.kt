package com.side.domain.modedl

import com.side.domain.model.Coordinate
import okhttp3.MultipartBody

data class PracticeRunRecord(
    var seq: Long = 0,
    var image: ByteArray,
    var startTime: String,
    var endTime: String,
    val runningDay: String,
    var runningTime: Int,
    var runningDistance: Int,
    var avgSpeed: Double,
    var calorie: Int = 0,
)