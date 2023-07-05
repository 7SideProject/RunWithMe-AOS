package com.side.domain.modedl

import okhttp3.MultipartBody

data class PracticeRunRecord(
    var seq: Int = 0,
    var image: MultipartBody.Part,
    var startTime: String,
    var endTime: String,
    var runningTime: Int,
    var runningDistance: Int,
    var avgSpeed: Double,
    var calorie: Int = 0,
)