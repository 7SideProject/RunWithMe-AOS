package com.side.runwithme.view.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.side.domain.usecase.datastore.GetUserSeqDataStoreUseCase
import com.side.domain.usecase.user.DailyCheckUseCase
import com.side.runwithme.R
import com.side.runwithme.base.MoveEventViewModel
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserSeqDataStoreUseCase: GetUserSeqDataStoreUseCase,
    private val dailyCheckUseCase: DailyCheckUseCase
): MoveEventViewModel<HomeViewModel.MoveEvent>() {

    private val _userSeq = MutableStateFlow<Long>(-1L)
    val userSeq = _userSeq.asStateFlow()

    private val _dailyCheckEvent = MutableEventFlow<DailyCheckEvent>()
    val dailyCheckEvent get() = _dailyCheckEvent.asEventFlow()

//    private val _moveScreenEventFlow = MutableEventFlow<Event>()
//    val moveScreenEventFlow get() = _moveScreenEventFlow.asEventFlow()
//
//
//    fun onClickChallengeList(){
//        viewModelScope.launch {
//            _moveScreenEventFlow.emit(Event.ChallengeListAction)
//        }
//    }

//    private val _flow = MutableStateFlow("")
//    fun dd(){
//        viewModelScopeLaunchMain { _flow.emit("dd") }
//    }

    fun onClickChallengeList(){
        Log.d("test123", "onClickChallengeList: ")
        emitMoveEventFlow(MoveEvent.ChallengeListAction)
    }

    fun onClickMyChallengeList(){
        emitMoveEventFlow(MoveEvent.MyChallengeListAction)
    }

    fun dailyCheckRequest(){
        if(userSeq.value != -1L){
            dailyCheck()
            return
        }
        getUserSeq()
    }
    private fun getUserSeq(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserSeqDataStoreUseCase().collectLatest {
                Log.d("getUserProfileError", "getUserProfile: $it")
                it.onSuccess { userSeq ->
                    _userSeq.value = userSeq
                    dailyCheck()
                }.onFailure { error->
                    Log.d("getUserSeqError", "${error} ")
                }.onError { error ->
                    Log.d("getUserSeqError", "${error.message} ")
                }
            }
        }
    }
    private fun dailyCheck(){
        viewModelScope.launch(Dispatchers.IO) {
            dailyCheckUseCase(userSeq.value).collectLatest {
                it.onSuccess {success->
                    if(success.data!!.isSuccess){
                        _dailyCheckEvent.emit(DailyCheckEvent.Response(R.string.message_daily_check))
                    }else{
                        _dailyCheckEvent.emit(DailyCheckEvent.Response(R.string.message_duplicate_today_daily_check))
                    }
                }.onFailure {fail ->
                    _dailyCheckEvent.emit(DailyCheckEvent.Fail(fail.message))
                }.onError {error->
                    Log.d("dailyCheckError", "${error.message}")
                }
            }
        }
    }

    sealed interface MoveEvent {
        object ChallengeListAction : MoveEvent
        object MyChallengeListAction : MoveEvent

    }

    sealed class DailyCheckEvent {
        data class Response(val message: Int): DailyCheckEvent()
        data class Fail(val message: String): DailyCheckEvent()
    }
}