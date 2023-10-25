package com.side.runwithme.mapper

import com.naver.maps.geometry.LatLng
import com.side.runwithme.model.Coordinates


fun List<LatLng>.mapperToCoordinatesList(): ArrayList<Coordinates> = this.run {
        return ArrayList(this.map {
            Coordinates(
                it.latitude,
                it.longitude
            )
        })
    }

fun Array<Coordinates>.mapperToNaverLatLngList(): List<LatLng> = this.run {
    return this.map {
        LatLng(
            it.latitude,
            it.longitude
        )
    }
}

fun Coordinates.mapperToNaverLatLng(): LatLng = this.run {
    LatLng(
        this.latitude,
        this.longitude
    )
}
