package com.side.runwithme.view.route_detail

import androidx.lifecycle.ViewModel
import com.side.runwithme.model.CoordinatesParcelable
import com.side.runwithme.model.RunRecordParcelable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RouteDetailViewModel : ViewModel() {

    private val _runRecord = MutableStateFlow<RunRecordParcelable>(
        RunRecordParcelable(
            runRecordSeq = 0,
            startTime = "",
            endTime = "",
            runningDay = "",
            runningTime = 0,
            runningDistance = 0,
            avgSpeed = 0.0,
            calorie = 0,
            successYN = ""
        )
    )
    val runRecord get() = _runRecord.asStateFlow()

    private val _coordinatesParcelable = MutableStateFlow<Array<CoordinatesParcelable>>(arrayOf())
    val coordinates get() = _coordinatesParcelable.asStateFlow()

    fun putRunRecord(runRecord: RunRecordParcelable){
        _runRecord.value = runRecord
    }

    fun putCoordinates(coordinates: Array<CoordinatesParcelable>){
        _coordinatesParcelable.value = coordinates
    }

}