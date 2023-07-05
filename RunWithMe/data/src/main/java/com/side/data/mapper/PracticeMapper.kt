package com.side.data.mapper

import com.side.data.entity.RunRecordEntity
import com.side.domain.modedl.PracticeRunRecord


fun List<RunRecordEntity>.mapperToPracitceRunRecord(): List<PracticeRunRecord> = this.run {
    return this.map {
        PracticeRunRecord(
            seq = it.seq,
            image = it.image,
            startTime = it.startTime,
            endTime = it.endTime,
            runningTime = it.runningTime,
            runningDistance = it.runningDistance,
            avgSpeed = it.avgSpeed,
            calorie = it.calorie
        )
    }
}

fun PracticeRunRecord.mapperToRunRecordEntity(): RunRecordEntity = this.run {
    return RunRecordEntity(
        seq = this.seq,
        image = this.image,
        startTime = this.startTime,
        endTime = this.endTime,
        runningTime = this.runningTime,
        runningDistance = this.runningDistance,
        avgSpeed = this.avgSpeed,
        calorie = this.calorie
    )
}