package com.side.runwithme.view.find_password

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.usecase.user.ChangePasswordUseCase
import com.side.runwithme.R
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.PasswordVerificationType
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.util.passwordValidation
import com.side.runwithme.view.join.JoinViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    val email: MutableStateFlow<String> = MutableStateFlow("")

    val password: MutableStateFlow<String> = MutableStateFlow("")

    val passwordConfirm: MutableStateFlow<String> = MutableStateFlow("")

    val verifyNumber = MutableStateFlow<String>("")

    private val _verifiedEmail = MutableStateFlow<String>("")
    val verifiedEmail = _verifiedEmail.asStateFlow()

    val doneState = combine(verifiedEmail, password, passwordConfirm) { verifiedEmail, password, passwordConfirm ->
        if(verifiedEmail.isBlank() || password.isBlank() || passwordConfirm.isBlank()){
            false
        }else password == passwordConfirm
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    private val _findPasswordEventFlow = MutableEventFlow<Event>()
    val findPasswordEventFlow = _findPasswordEventFlow.asEventFlow()


    fun verifyEmail(){
        _verifiedEmail.value = email.value
    }

    fun changePassword(){
        Log.d("test123", "changePassword: click")
        if(email.value.isBlank()){
            viewModelScope.launch {
                _findPasswordEventFlow.emit(Event.Fail("이메일을 입력해주세요."))
            }
            return
        }

        if(email.value != verifiedEmail.value){
            viewModelScope.launch {
                _findPasswordEventFlow.emit(Event.Fail("인증한 이메일과 다릅니다."))
            }
            return
        }

        when(passwordValidation(password.value, passwordConfirm.value)){
            PasswordVerificationType.LENGTH_ERROR -> {
                viewModelScope.launch {
                    _findPasswordEventFlow.emit(Event.Fail("비밀번호는 8~16자로 입력해주세요."))
                }
                return
            }
            PasswordVerificationType.NOT_EQUAL_ERROR -> {
                viewModelScope.launch {
                    _findPasswordEventFlow.emit(Event.Fail("비밀번호가 일치하지 않습니다."))
                }
                return
            }
            else -> {

            }
        }

        viewModelScope.launch(Dispatchers.IO){
            changePasswordUseCase(verifiedEmail.value, password.value).collectLatest {
                it.onSuccess {
                    _findPasswordEventFlow.emit(Event.Success("비밀번호 변경 성공"))
                }.onFailure {
                    _findPasswordEventFlow.emit(Event.Success("변경에 실패했습니다. 잠시 후 다시 시도해주세요."))
                }.onError {
                    Firebase.crashlytics.recordException(it)
                }
            }
        }
    }

    sealed class Event {
        data class Success(val message: String) : Event()
        data class Fail(val message: String) : Event()
    }
}