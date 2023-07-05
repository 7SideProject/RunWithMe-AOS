package com.side.runwithme.view.running_result

import androidx.lifecycle.ViewModel
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RunningResultViewModel @Inject constructor(

): ViewModel(){

    var runRecord: RunRecord = RunRecord(
        runRecordSeq = 0,
        runImageSeq = 0,
        runningDay = "",
        runningStartTime = "",
        runningEndTime = "",
        runningTime = 0,
        runningDistance = 0,
        runningAvgSpeed = 0.0,
        runningCalorieBurned = 0,
        runningStartingLat = 0.0,
        runningStartingLng = 0.0,
        completed = "",
        userName = "",
        userSeq = 0,
        challengeName = "",
        challengeSeq = 0
    )

    var imgFile: MultipartBody.Part? = null

    var coordinates: List<Coordinate> = listOf()

}