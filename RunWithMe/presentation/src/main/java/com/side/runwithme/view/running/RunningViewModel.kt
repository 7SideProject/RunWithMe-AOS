package com.side.runwithme.view.running

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.modedl.PracticeRunRecord
import com.side.domain.model.AllRunRecord
import com.side.domain.model.RunRecord
import com.side.domain.usecase.datastore.GetRunningInfoUseCase
import com.side.domain.usecase.datastore.GetUserWeightDataStoreUseCase
import com.side.domain.usecase.datastore.SaveRunningChallengeGoalAmountUseCase
import com.side.domain.usecase.datastore.SaveRunningChallengeGoalTypeUseCase
import com.side.domain.usecase.datastore.SaveRunningChallengeSeqUseCase
import com.side.domain.usecase.practice.InsertPracticeRunRecordUseCase
import com.side.domain.usecase.running.PostRunRecordUseCase
import com.side.domain.utils.onError
import com.side.domain.utils.onFailure
import com.side.domain.utils.onSuccess
import com.side.runwithme.util.GOAL_TYPE
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.util.getMutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningViewModel @Inject constructor(
    private val stateHandler: SavedStateHandle,
    private val postRunRecordUseCase: PostRunRecordUseCase,
    private val getUserWeightDataStoreUseCase: GetUserWeightDataStoreUseCase,
    private val insertPracticeRunRecordUseCase: InsertPracticeRunRecordUseCase,
    private val saveRunningChallengeSeqUseCase: SaveRunningChallengeSeqUseCase,
    private val saveRunningChallengeGoalAmountUseCase: SaveRunningChallengeGoalAmountUseCase,
    private val saveRunningChallengeGoalTypeUseCase: SaveRunningChallengeGoalTypeUseCase,
    private val getRunningInfoUseCase: GetRunningInfoUseCase
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

    private var _goalType = stateHandler.getMutableStateFlow<GOAL_TYPE>("goalType", GOAL_TYPE.TIME)
    val goalType
        get() = _goalType.asStateFlow()


    fun getMyWeight() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserWeightDataStoreUseCase().collect {
                it.onSuccess {
                    _weight.value = it
                }.onFailure {
                    _weight.value = 65
                }.onError {
                    Log.d("test123", "getMyWeight error:  $it")
                }
            }
        }
    }

    fun saveChallengeInfo(challengeSeq: Int, goalType: Int, goalAmount: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            saveRunningChallengeSeqUseCase(challengeSeq)
            saveRunningChallengeGoalAmountUseCase(goalAmount)
            saveRunningChallengeGoalTypeUseCase(goalType)
        }
    }

    fun getChallnegeInfo(){
        viewModelScope.launch(Dispatchers.IO) {
            getRunningInfoUseCase().collect {
                it.onSuccess {
                    _challengeSeq.value = it.challengeSeq
                    _goalAmount.value = it.goalAmount
                    if(it.goalType == GOAL_TYPE.TIME.ordinal) {
                        _goalType.value = GOAL_TYPE.TIME
                    }else{
                        _goalType.value = GOAL_TYPE.DISTANCE
                    }
                }.onFailure {
                    _postRunRecordEventFlow.emit(Event.GetDataStoreValuesError())
                }.onError {
                    _postRunRecordEventFlow.emit(Event.GetDataStoreValuesError())
                }
            }
        }
    }

    fun postPracticeRunRecord(runRecord: RunRecord, imgByteArray: ByteArray){
        viewModelScope.launch(Dispatchers.IO) {
            insertPracticeRunRecordUseCase(PracticeRunRecord(
                seq = 0,
                image = imgByteArray,
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
                    Log.d("test123", "postPracticeRunRecord fail: $it")
                    _postRunRecordEventFlow.emit(Event.Fail())
                }.onError {
                    Log.d("test123", "postPracticeRunRecord err: $it")
                    _postRunRecordEventFlow.emit(Event.Error())
                }
            }

        }
    }

    fun postChallengeRunRecord(allRunRecord: AllRunRecord){
        viewModelScope.launch(Dispatchers.IO) {
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
        class GetDataStoreValuesError : Event()
    }

}