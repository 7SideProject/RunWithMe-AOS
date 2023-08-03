package com.side.data.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import okhttp3.MultipartBody

@Entity(tableName = "run_table")
data class RunRecordEntity(
    @PrimaryKey(autoGenerate = true)
    var seq: Int = 0,
    var image: ByteArray,
    var startTime: String,
    var endTime: String,
    var runningTime: Int,
    var runningDistance: Int,
    var avgSpeed: Double,
    var calorie: Int = 0,
)
