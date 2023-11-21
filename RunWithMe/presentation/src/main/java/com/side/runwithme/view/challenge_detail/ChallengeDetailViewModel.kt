package com.side.runwithme.view.challenge_detail

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.data.util.getDecryptStringValue
import com.side.data.util.preferencesKeys
import com.side.domain.usecase.challenge.IsChallengeAlreadyJoinUseCase
import com.side.domain.usecase.challenge.JoinChallengeUseCase
import com.side.domain.usecase.challenge.LeaveChallengeUseCase
import com.side.domain.usecase.datastore.GetJWTDataStoreUseCase
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ChallengeDetailViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val isChallengeAlreadyJoinUseCase: IsChallengeAlreadyJoinUseCase,
    private val joinChallengeUseCase: JoinChallengeUseCase,
    private val leaveChallengeUseCase: LeaveChallengeUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _jwt = MutableStateFlow<String>("")
    val jwt = _jwt.asStateFlow()

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

    fun getJwtData(){
        viewModelScope.launch(Dispatchers.IO) {
            val flow = dataStore.getDecryptStringValue(preferencesKeys.JWT)
            flow.onEach {
                _jwt.value = it.toString()
            }.launchIn(viewModelScope)
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

    private fun goRunning(){
        viewModelScope.launch {
            /** 러닝 가능한지 확인 후 emit **/
//            _ChallengeDetailEventFlow.emit(Event.Success())
        }
    }


    fun joinChallenge(password: String? = null){
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

    fun quitChallenge(){
        // quit api 성공 시 ChallengeState Not_Join으로 변경
        viewModelScope.launch(Dispatchers.IO) {
            leaveChallengeUseCase(challenge.value!!.seq).collectLatest {
                it.onSuccess {
                    _ChallengeDetailEventFlow.emit(Event.DeleteChallenge())
                }.onFailure {
                    _ChallengeDetailEventFlow.emit(Event.Fail())

                }.onError {
                    Log.d("test123", "quitChallenge: err ${it}")
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