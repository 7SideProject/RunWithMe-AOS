package com.side.runwithme.view.challenge_list.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.usecase.challenge.IsChallengeAlreadyJoinUseCase
import com.side.domain.usecase.challenge.JoinChallengeUseCase
import com.side.domain.usecase.challenge.LeaveChallengeUseCase
import com.side.domain.usecase.user.GetUserProfileUseCase
import com.side.runwithme.model.ChallengeParcelable
import com.side.runwithme.util.CHALLENGE_STATE
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.util.onlyDateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ChallengeDetailViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val isChallengeAlreadyJoinUseCase: IsChallengeAlreadyJoinUseCase,
    private val joinChallengeUseCase: JoinChallengeUseCase,
    private val leaveChallengeUseCase: LeaveChallengeUseCase
) : ViewModel() {

    private val _challenge = MutableStateFlow<ChallengeParcelable?>(null)
    val challenge = _challenge.asStateFlow()

    private val isJoin = MutableStateFlow<Boolean>(false)

    val challengeState = combine(isJoin, challenge){ isJoin, challenge ->

        val dateEnd = challenge!!.dateEnd
        val dateStart = challenge.dateStart
        // 다음날로 지나갈 수도 있기 때문에 변경된 상황에서 측정하고자 함
        val today = LocalDate.now().onlyDateFormatter()

        if(dateEnd <= today){
            CHALLENGE_STATE.END
        }else if(isJoin && dateStart <= today){
            CHALLENGE_STATE.START
        }else if(isJoin && dateStart > today){
            CHALLENGE_STATE.NOT_START_AND_ALEADY_JOIN
        }else if(!isJoin && dateStart > today) {
            CHALLENGE_STATE.NOT_START_AND_NOT_JOIN
        }else {
            CHALLENGE_STATE.NOTHING
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), CHALLENGE_STATE.NOTHING)

    private val _ChallengeDetailEventFlow = MutableEventFlow<Event>()
    val ChallengeDetailEventFlow = _ChallengeDetailEventFlow.asEventFlow()


    fun setChallnege(challenge: ChallengeParcelable){
        _challenge.value = challenge

        getManagerProfile()
    }

    fun getManagerProfile(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserProfileUseCase(challenge.value!!.managerSeq).collect {
                it.onSuccess {
                    _challenge.value = _challenge.value!!.copy(managerName = it.data?.nickname ?: "")
                }.onFailure {

                }.onError {
                    Firebase.crashlytics.recordException(it)
                    Log.e("test123", "getManagerName: ", it)
                }
            }
        }
    }

    fun isChallengeAlreadyJoin(){
        viewModelScope.launch(Dispatchers.IO) {
            isChallengeAlreadyJoinUseCase(challenge.value!!.seq).collect {
                it.onSuccess {
                    isJoin.value = it.data
                }.onFailure {

                }.onError {
                    Firebase.crashlytics.recordException(it)
                    Log.e("test123", "isChallengeAlreadyJoin: ", it)
                }
            }
        }
    }

    fun onClickButton(){
        when(challengeState.value){
            CHALLENGE_STATE.START -> {
                goRunning()
            }
            CHALLENGE_STATE.NOT_START_AND_ALEADY_JOIN -> {
                quitChallenge()
            }
            CHALLENGE_STATE.NOT_START_AND_NOT_JOIN -> {
                joinChallenge()
            }
            else -> {

            }
        }

    }

    private fun goRunning(){
        viewModelScope.launch {
            /** 러닝 가능한지 확인 후 emit **/
//            _ChallengeDetailEventFlow.emit(Event.Success())
        }
    }


    /** api 구현해야함 **/
    private fun joinChallenge(){
        // join api 성공 시 ChallengeState AleadyJoin으로 변경
        viewModelScope.launch(Dispatchers.IO) {
            joinChallengeUseCase(challenge.value!!.seq, password).collectLatest {
                it.onSuccess {
                    isJoin.value = true
                }.onFailure {
                    /** 실패 처리 해야함 **/
                }.onError {
                    Firebase.crashlytics.recordException(it)
                }
            }
        }
    }

    private fun quitChallenge(){
        // quit api 성공 시 ChallengeState Not_Join으로 변경
        viewModelScope.launch(Dispatchers.IO) {
            leaveChallengeUseCase(challenge.value!!.seq).collectLatest {
                it.onSuccess {
                    isJoin.value = false

                }.onFailure {

                }.onError {
                    Firebase.crashlytics.recordException(it)
                }
            }
        }
    }



    sealed interface Event {
        class Success : Event
        class Fail : Event
        class DeleteChallenge : Event
    }
}