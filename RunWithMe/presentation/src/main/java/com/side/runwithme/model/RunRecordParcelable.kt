package com.side.runwithme.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class RunRecordParcelable(
    val runRecordSeq: Int,
    val runImageSeq: Int,
    val runningDay: String,
    val runningStartTime: String,
    val runningEndTime: String,
    val runningTime: Int,
    val runningDistance: Int,
    val runningAvgSpeed: Double,
    val runningCalorieBurned: Int,
    val runningStartingLat: Double,
    val runningStartingLng: Double,
    val completed: String,
    val userName: String = "",
    val userSeq: Int = 0,
    val challengeName: String = "",
    val challengeSeq: Int = 0,
): Parcelable