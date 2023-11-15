package com.side.runwithme.view.running

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.modedl.PracticeRunRecord
import com.side.domain.model.Coordinate
import com.side.domain.usecase.datastore.GetRunningInfoUseCase
import com.side.domain.usecase.datastore.GetUserWeightDataStoreUseCase
import com.side.domain.usecase.datastore.SaveRunningChallengeGoalAmountUseCase
import com.side.domain.usecase.datastore.SaveRunningChallengeGoalTypeUseCase
import com.side.domain.usecase.datastore.SaveRunningChallengeNameUseCase
import com.side.domain.usecase.datastore.SaveRunningChallengeSeqUseCase
import com.side.domain.usecase.practice.InsertPracticeRunRecordUseCase
import com.side.domain.usecase.running.PostRunRecordUseCase
import com.side.runwithme.mapper.mapperToCoordinate
import com.side.runwithme.mapper.mapperToRunRecordWithCoordinates
import com.side.runwithme.model.CoordinatesParcelable
import com.side.runwithme.model.RunRecordParcelable
import com.side.runwithme.util.GOAL_TYPE
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.util.getMutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
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
    private val saveRunningChallengeNameUseCase: SaveRunningChallengeNameUseCase,
    private val getRunningInfoUseCase: GetRunningInfoUseCase
) : ViewModel() {

    private val _postRunRecordEventFlow = MutableEventFlow<Event>()
    val postRunRecordEventFlow = _postRunRecordEventFlow.asEventFlow()

    private val _weight = stateHandler.getMutableStateFlow("weight", 65)
    val weight: StateFlow<Int> = _weight.asStateFlow()

    private val _challengeSeq = stateHandler.getMutableStateFlow("challengeSeq", 0L)
    val challengeSeq: StateFlow<Long> = _challengeSeq.asStateFlow()

    private val _challengeName = stateHandler.getMutableStateFlow("challengeName", "")
    val challengeName = _challengeName.asStateFlow()

    private var _goalAmount = stateHandler.getMutableStateFlow<Long>("goalAmount", 60000L)
    val goalAmount = _goalAmount.asStateFlow()

    private var _goalType = stateHandler.getMutableStateFlow<GOAL_TYPE>("goalType", GOAL_TYPE.TIME)
    val goalType = _goalType.asStateFlow()


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

    fun saveChallengeInfo(
        challengeSeq: Long,
        goalType: Int,
        goalAmount: Long,
        challengeName: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            saveRunningChallengeSeqUseCase(challengeSeq)
            saveRunningChallengeGoalAmountUseCase(goalAmount)
            saveRunningChallengeGoalTypeUseCase(goalType)
            saveRunningChallengeNameUseCase(challengeName)
        }
    }

    fun getChallnegeInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            getRunningInfoUseCase().collect {
                it.onSuccess {
                    _challengeSeq.value = it.challengeSeq
                    _challengeName.value = it.challengeName
                    _goalAmount.value = it.goalAmount
                    if (it.goalType == GOAL_TYPE.TIME.ordinal) {
                        _goalType.value = GOAL_TYPE.TIME
                    } else {
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

    fun checkIsOver10Seconds(runRecord: RunRecordParcelable): Boolean {
        if (runRecord.runningTime <= 10) {
            return false;
        }

        return true;
    }

    fun postPracticeRunRecord(runRecord: RunRecordParcelable, imgByteArray: ByteArray?) {
        if (!checkIsOver10Seconds(runRecord)) {
            viewModelScope.launch {
                _postRunRecordEventFlow.emit(Event.Success())
            }
        }

        if(imgByteArray == null){
            viewModelScope.launch {
                _postRunRecordEventFlow.emit(Event.Fail())
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            insertPracticeRunRecordUseCase(
                PracticeRunRecord(
                    seq = 0,
                    image = imgByteArray!!,
                    startTime = runRecord.startTime,
                    endTime = runRecord.endTime,
                    runningDay = runRecord.runningDay,
                    runningTime = runRecord.runningTime,
                    runningDistance = runRecord.runningDistance,
                    avgSpeed = runRecord.avgSpeed,
                    calorie = runRecord.calorie
                )
            ).collect {
                it.onSuccess {
                    Log.d("test123", "postPracticeRunRecord: success ")
                    _postRunRecordEventFlow.emit(Event.Success())
                }.onFailure {
                    Log.d("test123", "postPracticeRunRecord fail: $it")
                    _postRunRecordEventFlow.emit(Event.Fail())
                }.onError {
                    Log.d("test123", "postPracticeRunRecord err: $it")
                    Firebase.crashlytics.recordException(it)
                    _postRunRecordEventFlow.emit(Event.Error())
                }
            }

        }
    }

    fun postChallengeRunRecord(runRecord: RunRecordParcelable, coordinates: List<CoordinatesParcelable>, image: MultipartBody.Part?) {
        if (!checkIsOver10Seconds(runRecord)) {
            viewModelScope.launch {
                _postRunRecordEventFlow.emit(Event.Success())
            }
        }

        if(image == null){
            viewModelScope.launch {
                _postRunRecordEventFlow.emit(Event.Fail())
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            postRunRecordUseCase(challengeSeq.value, runRecord.mapperToRunRecordWithCoordinates(coordinates.mapperToCoordinate()), image!!).collect {
                it.onSuccess {
                    _postRunRecordEventFlow.emit(Event.Success())
                }.onFailure {
                    Log.d("test123", "postPracticeRunRecord fail: $it")
                    _postRunRecordEventFlow.emit(Event.Fail())
                }.onError {
                    // 서버 에러 분류해줄 수 있으면 좋을듯
                    Log.d("test123", "postPracticeRunRecord err: $it")
                    Firebase.crashlytics.recordException(it)
                    _postRunRecordEventFlow.emit(Event.ServerError())
                }

            }
        }
    }


    sealed class Event {
        class Success : Event()
        class Fail : Event()
        class ServerError : Event()
        class Error : Event()
        class GetDataStoreValuesError : Event()
    }

}