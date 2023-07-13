package com.side.runwithme.mapper

import com.side.domain.model.Coordinate
import com.side.runwithme.model.Coordinates

fun List<Coordinate>.mapperToCoordinates() : List<Coordinates> = this.run {
    return this.map {
        Coordinates(
            it.latitude,
            it.longitude
        )
    }
}

fun List<Coordinates>.mapperToCoordinate() : List<Coordinate> = this.run {
    return this.map {
        Coordinate(
            it.latitude,
            it.longitude
        )
    }
}
