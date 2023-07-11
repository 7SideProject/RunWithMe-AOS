package com.side.runwithme.model

import android.os.Parcel
import android.os.Parcelable
import com.side.domain.model.Coordinate

data class Coordinates (
    val latitude: Double,
    val longitude: Double
        ): Parcelable {


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coordinates> {
        override fun createFromParcel(parcel: Parcel): Coordinates {
            return Coordinates(parcel.readDouble(), parcel.readDouble())
        }

        override fun newArray(size: Int): Array<Coordinates?> {
            return arrayOfNulls(size)
        }
    }

}