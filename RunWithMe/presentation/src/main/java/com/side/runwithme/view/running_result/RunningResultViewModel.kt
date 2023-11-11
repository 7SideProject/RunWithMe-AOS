package com.side.runwithme.view.running_result

import androidx.lifecycle.ViewModel
import com.side.runwithme.model.CoordinatesParcelable
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
        startTime = "",
        endTime = "",
        runningDay = "",
        runningTime = 0,
        runningDistance = 0,
        avgSpeed = 0.0,
        calorie = 0,
        successYN = ""

    ))
    val runRecord get() = _runRecord.asStateFlow()

    private val _imgByteArray = MutableStateFlow<ByteArray?>(null)
    val imgByteArray get() = _imgByteArray.asStateFlow()

    private val _coordinatesParcelable = MutableStateFlow<Array<CoordinatesParcelable>>(arrayOf())
    val coordinates get() = _coordinatesParcelable.asStateFlow()

    private val _challengeName = MutableStateFlow<String>("")
    val challengeName = _challengeName.asStateFlow()

    fun putRunRecord(runRecord: RunRecordParcelable){
        _runRecord.value = runRecord
    }

    fun putImgByteArray(imgByteArray: ByteArray?){
        _imgByteArray.value = imgByteArray
    }

    fun putCoordinates(coordinates: Array<CoordinatesParcelable>){
        _coordinatesParcelable.value = coordinates
    }

    fun putChallengeName(challengeName: String){
        _challengeName.value = challengeName
    }
}