package com.side.runwithme.mapper

import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.runwithme.model.RunRecordParcelable

fun RunRecordParcelable.mapperToRunRecordWithCoordinates(coordinates: List<Coordinate>): RunRecord = this.run {
    RunRecord(
        runRecordSeq = this.runRecordSeq,
        startTime = this.startTime,
        endTime = this.endTime,
        runningDay = this.runningDay,
        runningTime = this.runningTime,
        runningDistance = this.runningDistance,
        avgSpeed = this.avgSpeed,
        calorie = this.calorie,
        successYN = this.successYN,
        coordinates = coordinates
    )
}