package com.side.runwithme.view.running

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.domain.model.AllRunRecord
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.domain.model.User
import com.side.domain.usecase.running.PostRunRecordUseCase
import com.side.domain.utils.ResultType
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
    private val dataStoreDataSource: DataStoreDataSource
): ViewModel(){

    private val _postRunRecordEventFlow = MutableEventFlow<Event>()
    val postRunRecordEventFlow get() = _postRunRecordEventFlow.asEventFlow()

    private val _weight = stateHandler.getMutableStateFlow("weight", 70)
    val weight: StateFlow<Int>
        get() = _weight.asStateFlow()

    private val _challengeSeq = stateHandler.getMutableStateFlow("challengeSeq", 0)
    val challengeSeq: StateFlow<Int>
        get() = _challengeSeq.asStateFlow()

    fun getMyWeight() {
        viewModelScope.launch {
            dataStoreDataSource.getUserWeight().collect {
                _weight.value = it
            }
        }
    }

    fun saveChallengeSeqInViewModel(challengeSeq: Int) {
        if(challengeSeq == 0) return

        _challengeSeq.value = challengeSeq
    }

    fun postRunRecord(allRunRecord: AllRunRecord){
        viewModelScope.launch {
            postRunRecordUseCase(challengeSeq.value, allRunRecord).collect {
                when(it){
                    is ResultType.Success -> {
                        _postRunRecordEventFlow.emit(Event.Success())
                    }
                    is ResultType.Fail -> {
                        _postRunRecordEventFlow.emit(Event.Fail())
                    }
                    is ResultType.Error -> {
                        // 서버 에러 분류해줄 수 있으면 좋을듯
                        _postRunRecordEventFlow.emit(Event.ServerError())
                    }
                    else -> {

                    }
                }
            }
        }
    }

    sealed class Event {
        class Success : Event()
        class Fail : Event()
        class ServerError : Event()
    }

}