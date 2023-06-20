package com.side.runwithme.view.running

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.AllRunRecord
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.domain.model.User
import com.side.domain.usecase.running.PostRunRecordUseCase
import com.side.domain.utils.ResultType
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RunningViewModel @Inject constructor(
    private val postRunRecordUseCase: PostRunRecordUseCase
): ViewModel(){

    private val _postRunRecordEventFlow = MutableEventFlow<Event>()
    val postRunRecordEventFlow get() = _postRunRecordEventFlow.asEventFlow()

    fun postRunRecord(challengeSeq: Int, allRunRecord: AllRunRecord){
        viewModelScope.launch {
            postRunRecordUseCase(challengeSeq, allRunRecord).collect {
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