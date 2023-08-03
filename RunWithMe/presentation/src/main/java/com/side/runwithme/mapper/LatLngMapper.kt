package com.side.runwithme.mapper

import com.naver.maps.geometry.LatLng
import com.side.domain.model.Coordinate


fun List<LatLng>.mapperToCoordinateList(): List<Coordinate> = this.run {
    return this.map {
        Coordinate(
            it.latitude,
            it.longitude
        )
    }
}

fun List<Coordinate>.mapperToNaverLatLngList(): List<LatLng> = this.run {
    return this.map {
        LatLng(
            it.latitude,
            it.longitude
        )
    }
}

fun Coordinate.mapperToNaverLatLng() : LatLng = this.run {
    return LatLng(
        this.latitude,
        this.longitude
    )
}