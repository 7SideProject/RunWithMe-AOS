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
import com.side.domain.usecase.datastore.GetUserSeqDataStoreUseCase
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
    private val dataStore: DataStore<Preferences>,
    private val getUserSeqDataStoreUseCase: GetUserSeqDataStoreUseCase
) : ViewModel() {

    private val _jwt = MutableStateFlow<String>("")
    val jwt = _jwt.asStateFlow()

    private var userSeq: Long = 0L

    private val _challenge = MutableStateFlow<ChallengeParcelable?>(null)
    val challenge = _challenge.asStateFlow()

    private val isJoin = MutableStateFlow<Boolean>(false)

    val challengeState = combine(isJoin, challenge) { isJoin, challenge ->

        val dateEnd = challenge!!.dateEnd
        val dateStart = challenge.dateStart
        // 다음날로 지나갈 수도 있기 때문에 변경된 상황에서 측정하고자 함
        val today = LocalDate.now().onlyDateFormatter()

        if (dateEnd <= today) {
            CHALLENGE_STATE.END
        } else if (isJoin && dateStart <= today) {
            CHALLENGE_STATE.START
        } else if (isJoin && dateStart > today) {
            if (challenge.managerSeq == userSeq) {
                CHALLENGE_STATE.NOT_START_AND_MANAGER
            } else {
                CHALLENGE_STATE.NOT_START_AND_ALEADY_JOIN
            }
        } else if (!isJoin && dateStart > today) {
            CHALLENGE_STATE.NOT_START_AND_NOT_JOIN
        } else {
            CHALLENGE_STATE.NOTHING
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), CHALLENGE_STATE.NOTHING)


    private val _ChallengeDetailEventFlow = MutableEventFlow<Event>()
    val ChallengeDetailEventFlow = _ChallengeDetailEventFlow.asEventFlow()


    fun setChallnege(challenge: ChallengeParcelable) {
        _challenge.value = challenge

        getManagerProfile()
        getUserSeq()
    }

    private fun getUserSeq() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserSeqDataStoreUseCase().collectLatest {
                it.onSuccess {
                    userSeq = it
                }.onError {
                    Firebase.crashlytics.recordException(it)
                    _ChallengeDetailEventFlow.emit(Event.Fail("서버 에러 입니다. 다시 시도해주세요."))
                }
            }
        }
    }

    private fun getManagerProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserProfileUseCase(challenge.value!!.managerSeq).collect {
                it.onSuccess {
                    _challenge.value =
                        _challenge.value!!.copy(managerSeq = it.data?.seq ?: 0L)
                }.onFailure {

                }.onError {
                    Firebase.crashlytics.recordException(it)
                    _ChallengeDetailEventFlow.emit(Event.Fail("서버 에러 입니다. 다시 시도해주세요."))
                }
            }
        }
    }

    fun getJwtData() {
        viewModelScope.launch(Dispatchers.IO) {
            val flow = dataStore.getDecryptStringValue(preferencesKeys.JWT)
            flow.onEach {
                _jwt.value = it.toString()
            }.launchIn(viewModelScope)
        }
    }

    fun isChallengeAlreadyJoin() {
        viewModelScope.launch(Dispatchers.IO) {
            isChallengeAlreadyJoinUseCase(challenge.value!!.seq).collect {
                it.onSuccess {
                    isJoin.value = it.data
                }.onFailure {

                }.onError {
                    Firebase.crashlytics.recordException(it)
                    _ChallengeDetailEventFlow.emit(Event.Fail("서버 에러 입니다. 다시 시도해주세요."))
                }
            }
        }
    }

    fun joinChallenge(password: String? = null) {
        // join api 성공 시 ChallengeState AleadyJoin으로 변경
        viewModelScope.launch(Dispatchers.IO) {
            joinChallengeUseCase(challenge.value!!.seq, password).collectLatest {
                it.onSuccess {
                    isJoin.value = true
                    _ChallengeDetailEventFlow.emit(Event.JoinSuccess())
                }.onFailure {
                    _ChallengeDetailEventFlow.emit(Event.Fail(it.message))
                }.onError {
                    Firebase.crashlytics.recordException(it)
                    _ChallengeDetailEventFlow.emit(Event.Fail("서버 에러 입니다. 다시 시도해주세요."))
                }
            }
        }
    }

    fun quitChallenge() {
        // quit api 성공 시 ChallengeState Not_Join으로 변경
        viewModelScope.launch(Dispatchers.IO) {
            leaveChallengeUseCase(challenge.value!!.seq).collectLatest {
                it.onSuccess {
                    _ChallengeDetailEventFlow.emit(Event.DeleteChallenge())
                }.onFailure {
                    _ChallengeDetailEventFlow.emit(Event.Fail(it.message))
                }.onError {
                    Firebase.crashlytics.recordException(it)
                    _ChallengeDetailEventFlow.emit(Event.Fail("서버 에러 입니다. 다시 시도해주세요."))
                }
            }
        }
    }


    sealed interface Event {
        class Success : Event
        data class Fail(val message: String) : Event
        class DeleteChallenge : Event
        class JoinSuccess : Event
    }
}