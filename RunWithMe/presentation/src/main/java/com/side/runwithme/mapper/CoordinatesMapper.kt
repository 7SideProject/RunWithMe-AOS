package com.side.runwithme.mapper

import com.side.domain.model.Coordinate
import com.side.runwithme.model.CoordinatesParcelable

fun List<CoordinatesParcelable>.mapperToCoordinate() : List<Coordinate> = this.run {
    return this.map {
        Coordinate(
            (it.latitude * 1_000_000).toInt(),
            (it.longitude * 1_000_000).toInt()
        )
    }
}