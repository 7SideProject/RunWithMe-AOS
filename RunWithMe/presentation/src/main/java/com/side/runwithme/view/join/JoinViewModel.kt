package com.side.runwithme.view.join

import android.util.Log
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.User
import com.side.domain.usecase.user.JoinUseCase
import com.side.domain.utils.ResultType
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
    private val joinUseCase: JoinUseCase
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

    private val _joinEventFlow = MutableEventFlow<Event>()
    val joinEventFlow get() = _joinEventFlow.asEventFlow()

    private val _join2EventFlow = MutableEventFlow<PasswordVerificationType>()
    val join2EventFlow get() = _join2EventFlow.asEventFlow()

    private val _idIsDuplicateEventFlow = MutableEventFlow<Int>()
    val idIsDuplicateEventFlow get() = _idIsDuplicateEventFlow.asEventFlow()

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

    fun join() {
        if(!matchesNickNameRule(nickname.value)){
            viewModelScope.launch {
                _joinEventFlow.emit(Event.Fail("닉네임은 한글, 영문, 숫자로만 2자~8자까지 입력 가능합니다."))
            }
            return
        }


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
                when (it) {
                    is ResultType.Success -> {
                        _joinEventFlow.emit(Event.Success(it.data.message))
                    }
                    is ResultType.Fail -> {
                        _joinEventFlow.emit(Event.Fail(it.data.message))
                    }
                    is ResultType.Error -> {
                        Log.d("joinError", "${it.exception.message} ")
                    }else->{

                    }
                }
            }
        }
    }

    fun checkIdIsDuplicate(){
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    //한글, 영문, 숫자로만 2자~8자까지 입력 가능
    fun matchesNickNameRule(nickName: String): Boolean{
        val pattern = "^[a-zA-Z0-9가-힣]+$".toRegex()

        if(nickName.length in 2..8 && pattern.matches(nickName)){
            return true
        }

        return false
    }

    sealed class Event {
        data class Success(val message: String) : Event()
        data class Fail(val message: String) : Event()
    }
}