package com.side.runwithme.model

import android.os.Parcelable
import com.side.domain.model.Coordinate
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RunRecordParcelable(
    val runRecordSeq: Long,
    val startTime: String,
    val endTime: String,
    val runningDay: String,
    val runningTime: Int,
    val runningDistance: Int,
    val avgSpeed: Double,
    val calorie: Int,
    val successYN: String
): Parcelable