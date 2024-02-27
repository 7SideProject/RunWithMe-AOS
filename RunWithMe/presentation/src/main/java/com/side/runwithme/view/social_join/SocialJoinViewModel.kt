package com.side.runwithme.view.social_join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.model.Profile
import com.side.domain.usecase.user.CheckNicknameIsDuplicateUseCase
import com.side.domain.usecase.user.JoinSocialUserUseCase
import com.side.runwithme.R
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.util.matchesNickNameRule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialJoinViewModel @Inject constructor(
    private val joinSocialUserUseCase: JoinSocialUserUseCase,
    private val checkNicknameIsDuplicateUseCase: CheckNicknameIsDuplicateUseCase
) : ViewModel() {

    private var userSeq: Long = 0L

    val nickname: MutableStateFlow<String> = MutableStateFlow("")

    private val _weight = MutableStateFlow<Int>(0)
    val weight get() = _weight.asStateFlow()

    private val _height = MutableStateFlow<Int>(0)
    val height get() = _height.asStateFlow()

    val allDone : StateFlow<Boolean> = combine(height, nickname, weight){ height, nickname, weight ->
        height != 0 && weight != 0 && nickname.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    private val _joinEventFlow = MutableEventFlow<JoinEvent>()
    val joinEventFlow get() = _joinEventFlow.asEventFlow()

    fun setHeight(height: Int){
        this._height.update { height }
    }

    fun setWeight(weight: Int){
        this._weight.update { weight }
    }

    fun setUserSeq(seq: Long){
        this.userSeq = seq
    }

    fun joinInvalidCheck() {
        if(!matchesNickNameRule(nickname.value)){
            viewModelScope.launch {
                _joinEventFlow.emit(JoinEvent.Fail(R.string.message_invalid_nickname))
            }
        }else{
            checkNicknameIsDuplicate()
        }
    }

    fun join() {

        val profile = Profile(
            nickname = nickname.value,
            height = height.value,
            weight = weight.value
        )

        viewModelScope.launch(Dispatchers.IO) {
            joinSocialUserUseCase(userSeq, profile).collectLatest {
                it.onSuccess {
                    _joinEventFlow.emit(JoinEvent.Success(R.string.message_success_join))
                }.onFailure {
                    _joinEventFlow.emit(JoinEvent.Fail(R.string.message_fail_join))
                }.onError { error ->
                    _joinEventFlow.emit(JoinEvent.Error())
                    Firebase.crashlytics.recordException(error)
                }
            }
        }
    }

    private fun checkNicknameIsDuplicate(){
        viewModelScope.launch(Dispatchers.IO) {
            checkNicknameIsDuplicateUseCase(nickname.value).collectLatest {
                it.onSuccess {success->
                    // 중복시 true, 중복아니면 false
                    if(success.data.isDuplicated){
                        _joinEventFlow.emit(JoinEvent.Fail(R.string.message_duplicate_nickname))
                    }else{
                        _joinEventFlow.emit(JoinEvent.Check())
                    }
                }.onFailure {fail ->
                    _joinEventFlow.emit(JoinEvent.Fail(R.string.join_error))
                }.onError {error->
                    Firebase.crashlytics.recordException(error)
                    _joinEventFlow.emit(JoinEvent.Error())
                }
            }
        }
    }

    sealed class JoinEvent {
        data class Success(val message: Int) : JoinEvent()
        data class Check(val check: Boolean = true): JoinEvent()
        data class Fail(val message: Int) : JoinEvent()
        class Error : JoinEvent()
    }
}