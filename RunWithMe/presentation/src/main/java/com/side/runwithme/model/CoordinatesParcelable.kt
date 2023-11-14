package com.side.runwithme.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoordinatesParcelable (
    val latitude: Double,
    val longitude: Double
): Parcelable