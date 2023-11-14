package com.side.runwithme.mapper

import com.side.domain.model.Coordinate
import com.side.runwithme.model.CoordinatesParcelable

fun ArrayList<Coordinate>.mapperToCoordinatesParcelable() : ArrayList<CoordinatesParcelable> = this.run {
    val coordinatesParcelableList = arrayListOf<CoordinatesParcelable>()
    for(coordinate in this){
        coordinatesParcelableList.add(CoordinatesParcelable(coordinate.latitude, coordinate.longitude))
    }
    coordinatesParcelableList
}

fun List<CoordinatesParcelable>.mapperToCoordinate() : List<Coordinate> = this.run {
    return this.map {
        Coordinate(
            it.latitude,
            it.longitude
        )
    }
}
