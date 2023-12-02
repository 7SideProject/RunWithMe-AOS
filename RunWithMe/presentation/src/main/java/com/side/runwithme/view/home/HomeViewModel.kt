package com.side.runwithme.view.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.side.domain.usecase.datastore.GetUserSeqDataStoreUseCase
import com.side.domain.usecase.user.DailyCheckUseCase
import com.side.runwithme.R
import com.side.runwithme.base.BaseViewModel
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
): BaseViewModel<HomeViewModel.MoveEvent>() {

    private val _userSeq = MutableStateFlow<Long>(-1L)
    val userSeq = _userSeq.asStateFlow()

    private val _dailyCheckEvent = MutableEventFlow<DailyCheckEvent>()
    val dailyCheckEvent get() = _dailyCheckEvent.asEventFlow()

    fun onClickChallengeList(){
        viewModelScope.launch {
            emitMsgEventFlow(MoveEvent.ChallengeListAction)
        }
    }

    fun onClickMyChallengeList(){
        viewModelScope.launch {
            emitMsgEventFlow(MoveEvent.MyChallengeListAction)
        }
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
                    _dailyCheckEvent.emit(DailyCheckEvent.Fail("다시 시도해주세요."))
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