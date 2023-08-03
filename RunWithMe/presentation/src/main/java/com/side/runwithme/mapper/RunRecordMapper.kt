package com.side.runwithme.mapper

import com.side.domain.model.RunRecord
import com.side.runwithme.model.RunRecordParcelable

fun RunRecordParcelable.mapperToRunRecord(): RunRecord = this.run {
    RunRecord(
        runRecordSeq = this.runRecordSeq,
        runImageSeq = this.runImageSeq,
        runningDay = this.runningDay,
        runningStartTime = this.runningStartTime,
        runningEndTime = this.runningEndTime,
        runningTime = this.runningTime,
        runningDistance = this.runningDistance,
        runningAvgSpeed = this.runningAvgSpeed,
        runningCalorieBurned = this.runningCalorieBurned,
        runningStartingLat = this.runningStartingLat,
        runningStartingLng = this.runningStartingLng,
        completed = this.completed,
        userName = this.userName,
        userSeq = this.userSeq,
        challengeName = this.challengeName,
        challengeSeq = this.challengeSeq
    )
}

fun RunRecord.mapperToRunRecordParcelable(): RunRecordParcelable = this.run {
    RunRecordParcelable(
        runRecordSeq = this.runRecordSeq,
        runImageSeq = this.runImageSeq,
        runningDay = this.runningDay,
        runningStartTime = this.runningStartTime,
        runningEndTime = this.runningEndTime,
        runningTime = this.runningTime,
        runningDistance = this.runningDistance,
        runningAvgSpeed = this.runningAvgSpeed,
        runningCalorieBurned = this.runningCalorieBurned,
        runningStartingLat = this.runningStartingLat,
        runningStartingLng = this.runningStartingLng,
        completed = this.completed,
        userName = this.userName,
        userSeq = this.userSeq,
        challengeName = this.challengeName,
        challengeSeq = this.challengeSeq
    )
}