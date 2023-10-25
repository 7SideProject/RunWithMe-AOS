package com.side.runwithme.view.join

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.User
import com.side.domain.usecase.user.CheckIdIsDuplicateUseCase
import com.side.domain.usecase.user.CheckNicknameIsDuplicateUseCase
import com.side.domain.usecase.user.JoinUseCase
import com.side.domain.utils.onError
import com.side.domain.utils.onFailure
import com.side.domain.utils.onSuccess
import com.side.runwithme.R
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.PasswordVerificationType
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.util.passwordValidation
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
class JoinViewModel @Inject constructor(
    private val joinUseCase: JoinUseCase,
    private val checkIdIsDuplicateUseCase: CheckIdIsDuplicateUseCase,
    private val checkNicknameIsDuplicateUseCase: CheckNicknameIsDuplicateUseCase
) : ViewModel() {

    val id: MutableStateFlow<String> = MutableStateFlow("")

    val password: MutableStateFlow<String> = MutableStateFlow("")

    val passwordConfirm: MutableStateFlow<String> = MutableStateFlow("")

    val nickname: MutableStateFlow<String> = MutableStateFlow("")

    private val _weight = MutableStateFlow<Int>(0)
    val weight get() = _weight.asStateFlow()

    private val _height = MutableStateFlow<Int>(0)
    val height get() = _height.asStateFlow()

    val verifyNumber = MutableStateFlow<String>("")

    val allDone : StateFlow<Boolean> = combine(height, nickname, weight){ height, nickname, weight ->
            Log.d("test123", "combine: ${height}, ${nickname}, $weight")
            height != 0 && weight != 0 && nickname.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    private val _join3EventFlow = MutableEventFlow<JoinEvent>()
    val join3EventFlow get() = _join3EventFlow.asEventFlow()

    private val _join2EventFlow = MutableEventFlow<PasswordVerificationType>()
    val join2EventFlow get() = _join2EventFlow.asEventFlow()

    private val _idIsDuplicateEventFlow = MutableEventFlow<IdCheckEvent>()
    val idIsDuplicateEventFlow get() = _idIsDuplicateEventFlow.asEventFlow()

    private val pattern = "^[a-zA-Z0-9가-힣]+$".toRegex()

    fun setHeight(height: Int){
        this._height.update { height }
    }

    fun setWeight(weight: Int){
        this._weight.update { weight }
    }

    fun clickJoin2NextButton(){
        viewModelScope.launch {
            _join2EventFlow.emit(passwordValidation(password.value, passwordConfirm.value))
        }
    }

    fun joinInvalidCheck() {
        if(!matchesNickNameRule(nickname.value)){
            viewModelScope.launch {
                _join3EventFlow.emit(JoinEvent.Fail(R.string.message_invalid_nickname))
            }
        }else{
            checkNicknameIsDuplicate()
        }
    }

    fun join() {
        val user = User(
            seq = 0L,
            email = id.value,
            password = password.value,
            nickname = nickname.value,
            height = height.value,
            weight = weight.value,
            point = 0
        )

        viewModelScope.launch(Dispatchers.IO) {
            joinUseCase(user).collectLatest {
                it.onSuccess {
                    _join3EventFlow.emit(JoinEvent.Success(R.string.message_success_join))
                }.onFailure {
                    _join3EventFlow.emit(JoinEvent.Fail(R.string.message_fail_join))
                }.onError { error ->
                    Log.d("joinError", "${error.message} ")
                }
            }
        }
    }

    fun checkIdIsDuplicate(){
        viewModelScope.launch(Dispatchers.IO) {
            checkIdIsDuplicateUseCase(id.value).collectLatest {
                it.onSuccess {success->
                    // 중복시 true, 중복아니면 false
                    if(success.data.isDuplicated){
                        _idIsDuplicateEventFlow.emit(IdCheckEvent.Fail(R.string.message_duplicate_id))
                    }else{
                        _idIsDuplicateEventFlow.emit(IdCheckEvent.Success())
                    }
                }.onFailure {fail ->
                    Log.d("checkIdError", fail.message)
                }.onError {error->
                    Log.d("checkIdError", "${error.message}")
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
                        _join3EventFlow.emit(JoinEvent.Fail(R.string.message_duplicate_nickname))
                    }else{
                        _join3EventFlow.emit(JoinEvent.Check())
                    }
                }.onFailure {fail ->
                    Log.d("checkNickNameError", fail.message)
                }.onError {error->
                    Log.d("checkNickNameError", "${error.message}")
                }
            }
        }
    }

    //한글, 영문, 숫자로만 2자~8자까지 입력 가능
    fun matchesNickNameRule(nickName: String): Boolean{
        if(nickName.length in 2..8 && pattern.matches(nickName)){
            return true
        }

        return false
    }

    sealed class JoinEvent {
        data class Success(val message: Int) : JoinEvent()
        data class Check(val check: Boolean = true): JoinEvent()
        data class Fail(val message: Int) : JoinEvent()
    }

    sealed class IdCheckEvent {
        data class Success(val check: Boolean = true) : IdCheckEvent()
        data class Fail(val message: Int) : IdCheckEvent()
    }
}