package com.side.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class RunRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val seq: Long = 0,
    val image: ByteArray,
    val startTime: String,
    val endTime: String,
    val runningDay: String,
    val runningTime: Int,
    val runningDistance: Int,
    val avgSpeed: Double,
    val calorie: Int = 0,
)
