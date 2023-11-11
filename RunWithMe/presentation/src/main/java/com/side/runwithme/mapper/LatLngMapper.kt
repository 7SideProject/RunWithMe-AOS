package com.side.runwithme.mapper

import com.naver.maps.geometry.LatLng
import com.side.domain.model.Coordinate
import com.side.runwithme.model.CoordinatesParcelable


fun List<LatLng>.mapperToCoordinateList(): ArrayList<Coordinate> = this.run {
        return ArrayList(this.map {
            Coordinate(
                it.latitude,
                it.longitude
            )
        })
    }

fun Array<CoordinatesParcelable>.mapperToNaverLatLngList(): List<LatLng> = this.run {
    return this.map {
        LatLng(
            it.latitude,
            it.longitude
        )
    }
}

fun CoordinatesParcelable.mapperToNaverLatLng(): LatLng = this.run {
    LatLng(
        this.latitude,
        this.longitude
    )
}
