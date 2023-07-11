package com.side.runwithme.view.running

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.domain.modedl.PracticeRunRecord
import com.side.domain.model.AllRunRecord
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.domain.model.User
import com.side.domain.usecase.datastore.GetUserWeightDataStoreUseCase
import com.side.domain.usecase.practice.InsertPracticeRunRecordUseCase
import com.side.domain.usecase.running.PostRunRecordUseCase
import com.side.domain.utils.ResultType
import com.side.domain.utils.onError
import com.side.domain.utils.onFailure
import com.side.domain.utils.onSuccess
import com.side.runwithme.util.GOAL_TYPE_TIME
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.util.getMutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RunningViewModel @Inject constructor(
    private val stateHandler: SavedStateHandle,
    private val postRunRecordUseCase: PostRunRecordUseCase,
    private val getUserWeightDataStoreUseCase: GetUserWeightDataStoreUseCase,
    private val insertPracticeRunRecordUseCase: InsertPracticeRunRecordUseCase
): ViewModel(){

    private val _postRunRecordEventFlow = MutableEventFlow<Event>()
    val postRunRecordEventFlow get() = _postRunRecordEventFlow.asEventFlow()

    private val _weight = stateHandler.getMutableStateFlow("weight", 65)
    val weight: StateFlow<Int>
        get() = _weight.asStateFlow()

    private val _challengeSeq = stateHandler.getMutableStateFlow("challengeSeq", 0)
    val challengeSeq: StateFlow<Int>
        get() = _challengeSeq.asStateFlow()

    private var _goalAmount = stateHandler.getMutableStateFlow<Long>("goalAmount", 60000L)
    val goalAmount
        get() = _goalAmount.asStateFlow()

    private var _goalType = stateHandler.getMutableStateFlow<String>("goalType", GOAL_TYPE_TIME)
    val goalType
        get() = _goalType.asStateFlow()


    fun getMyWeight() {
        viewModelScope.launch {
            getUserWeightDataStoreUseCase().collect {
                it.onSuccess {
                    _weight.value = it
                }.onFailure {
                    _weight.value = 65
                }.onError {
                    Log.d("test123", "getMyWeight: $it")
                }
            }
        }
    }

    fun saveChallengeInfoInViewModel(challengeSeq: Int, goalType: String, goalAmount: Long) {
        if(challengeSeq == 0){
            return
        }

        _challengeSeq.value = challengeSeq
        _goalAmount.value = goalAmount
        _goalType.value = goalType
    }

    fun postRunRecord(allRunRecord: AllRunRecord){
        if(challengeSeq.value == -1){
            postPracticeRunRecord(allRunRecord)
        }else{
            postChallengeRunRecord(allRunRecord)
        }
    }

    private fun postPracticeRunRecord(allRunRecord: AllRunRecord){
        viewModelScope.launch {
            val runRecord = allRunRecord.runRecord
            insertPracticeRunRecordUseCase(PracticeRunRecord(
                seq = 0,
                image = allRunRecord.imgFile,
                startTime = runRecord.runningStartTime,
                endTime = runRecord.runningEndTime,
                runningTime = runRecord.runningTime,
                runningDistance = runRecord.runningDistance,
                avgSpeed = runRecord.runningAvgSpeed,
                calorie = runRecord.runningCalorieBurned
            )).collect {
                it.onSuccess {
                    _postRunRecordEventFlow.emit(Event.Success())
                }.onFailure {
                    _postRunRecordEventFlow.emit(Event.Fail())
                }.onError {
                    _postRunRecordEventFlow.emit(Event.Error())
                }
            }

        }
    }

    private fun postChallengeRunRecord(allRunRecord: AllRunRecord){
        viewModelScope.launch {
            postRunRecordUseCase(challengeSeq.value, allRunRecord).collect {
                it.onSuccess {
                    _postRunRecordEventFlow.emit(Event.Success())
                }.onFailure {
                    _postRunRecordEventFlow.emit(Event.Fail())
                }.onError {
                    // 서버 에러 분류해줄 수 있으면 좋을듯
                    _postRunRecordEventFlow.emit(Event.ServerError())
                }

            }
        }
    }


    sealed class Event {
        class Success : Event()
        class Fail : Event()
        class ServerError : Event()
        class Error: Event()
    }

}