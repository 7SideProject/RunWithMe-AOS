package com.side.runwithme.view.running_result

import androidx.lifecycle.ViewModel
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.runwithme.model.Coordinates
import com.side.runwithme.model.RunRecordParcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RunningResultViewModel @Inject constructor(

): ViewModel(){

    private val _runRecord = MutableStateFlow<RunRecordParcelable>(RunRecordParcelable(
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
    ))
    val runRecord get() = _runRecord.asStateFlow()

    private val _imgByteArray = MutableStateFlow<ByteArray?>(null)
    val imgByteArray get() = _imgByteArray.asStateFlow()

    private val _coordinates = MutableStateFlow<Array<Coordinates>>(arrayOf())
    val coordinates get() = _coordinates.asStateFlow()

    private val _challengeName = MutableStateFlow<String>("")
    val challengeName = _challengeName.asStateFlow()

    fun putRunRecord(runRecord: RunRecordParcelable){
        _runRecord.value = runRecord
    }

    fun putImgByteArray(imgByteArray: ByteArray?){
        _imgByteArray.value = imgByteArray
    }

    fun putCoordinates(coordinates: Array<Coordinates>){
        _coordinates.value = coordinates
    }

    fun putChallengeName(challengeName: String){
        _challengeName.value = challengeName
    }
}