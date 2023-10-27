package com.side.runwithme.view.route_detail

import androidx.lifecycle.ViewModel
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.runwithme.model.Coordinates
import com.side.runwithme.model.RunRecordParcelable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RouteDetailViewModel : ViewModel() {

    private val _runRecord = MutableStateFlow<RunRecordParcelable>(
        RunRecordParcelable(
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
    )
    val runRecord get() = _runRecord.asStateFlow()

    private val _coordinates = MutableStateFlow<Array<Coordinates>>(arrayOf())
    val coordinates get() = _coordinates.asStateFlow()

    fun putRunRecord(runRecord: RunRecordParcelable){
        _runRecord.value = runRecord
    }


    fun putCoordinates(coordinates: Array<Coordinates>){
        _coordinates.value = coordinates

//        val list = mutableListOf<Coordinate>()
////        list.add(Coordinate(37.3673347, 126.9319493))
////        list.add(Coordinate(37.3673357, 126.9319503))
////        list.add(Coordinate(37.3673367, 126.9319513))
////        list.add(Coordinate(37.3673377, 126.9319523))
////        list.add(Coordinate(37.3673387, 126.9319533))
////        list.add(Coordinate(37.3673397, 126.9319543))
////        list.add(Coordinate(37.3673407, 126.9319553))
////        list.add(Coordinate(37.3673417, 126.9319563))
////        list.add(Coordinate(37.3673427, 126.9319573))
////        list.add(Coordinate(37.3673437, 126.9319583))
////        list.add(Coordinate(37.3673447, 126.9319593))
////        list.add(Coordinate(37.3673457, 126.9319603))
////        list.add(Coordinate(37.3673467, 126.9319613))
////        list.add(Coordinate(37.3673477, 126.9319623))
////        list.add(Coordinate(37.3673487, 126.9319633))
////        list.add(Coordinate(37.3673497, 126.9319643))
////        list.add(Coordinate(37.3673507, 126.9319653))
////        list.add(Coordinate(37.3673517, 126.9319663))
////        list.add(Coordinate(37.3673527, 126.9319673))
////        list.add(Coordinate(37.3673537, 126.9319683))
////        list.add(Coordinate(37.3673547, 126.9319693))
////        list.add(Coordinate(37.3673557, 126.9319703))
////        list.add(Coordinate(37.3673567, 126.9319713))
////        list.add(Coordinate(37.3673577, 126.9319723))
//        list.add(Coordinate(37.3673587, 126.9319733))
//        list.add(Coordinate(37.3673597, 126.9320743))
//        list.add(Coordinate(37.3673607, 126.9321753))
//        list.add(Coordinate(37.3674617, 126.9322763))
//        list.add(Coordinate(37.3675617, 126.9323763))
//        list.add(Coordinate(37.3676617, 126.9324763))
//        list.add(Coordinate(37.3677617, 126.9325763))
//        list.add(Coordinate(37.3678617, 126.9326763))
//        list.add(Coordinate(37.3679617, 126.9327763))
//        list.add(Coordinate(37.3680617, 126.9328763))
//        list.add(Coordinate(37.3681617, 126.9329763))
//        list.add(Coordinate(37.3682617, 126.9330763))
//        list.add(Coordinate(37.3683617, 126.9331763))
//        list.add(Coordinate(37.3684617, 126.9332763))
//        list.add(Coordinate(37.3685617, 126.9333763))
//        list.add(Coordinate(37.3686617, 126.9334763))
//        _coordinates.value = list
    }

}