package com.side.runwithme.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.model.User
import com.side.domain.usecase.datastore.GetJWTDataStoreUseCase
import com.side.domain.usecase.datastore.GetPermissionCheckUseCase
import com.side.domain.usecase.datastore.SavePermissionCheckUseCase
import com.side.domain.usecase.user.LoginWithEmailUseCase
import com.side.domain.usecase.user.LoginWithKakaoUseCase
import com.side.domain.utils.ResultType
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val savePermissionCheckUseCase: SavePermissionCheckUseCase,
    private val getPermissionCheckUseCase: GetPermissionCheckUseCase,
    private val getJWTDataStoreUseCase: GetJWTDataStoreUseCase,
    private val loginWithKakaoUseCase: LoginWithKakaoUseCase
) : ViewModel() {

    val email: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")

    private val _loginEventFlow = MutableEventFlow<Event>()
    val loginEventFlow get() = _loginEventFlow.asEventFlow()

    private val _permissionEventFlow = MutableEventFlow<Boolean>()
    val permissionEventFlow = _permissionEventFlow.asEventFlow()

    fun loginWithEmail() {
        if (email.value.isBlank() || password.value.isBlank()) {
            viewModelScope.launch {
                _loginEventFlow.emit(Event.Fail("이메일, 비밀번호를 입력해주세요."))
            }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            loginWithEmailUseCase(User(email.value, password.value)).collectLatest {
                it.onSuccess {response ->
                    _loginEventFlow.emit(Event.Success())
                }.onFailure {response ->
                    _loginEventFlow.emit(Event.Fail(response.message))
                }.onError { exception ->
                    _loginEventFlow.emit(Event.Fail("서버 에러입니다. 다시 시도해주세요."))
                    Firebase.crashlytics.recordException(exception)
                }
            }
        }
    }

    fun loginWithKakao(accessToken: String) {
        viewModelScope.launch {
            loginWithKakaoUseCase(accessToken).collectLatest {
                it.onSuccess { response ->
                    if (response.data == null) {
                        ResultType.Fail("다시 시도해주세요.")
                        return@collectLatest
                    }

                    if (response.data!!.isInitialized) {
                        _loginEventFlow.emit(Event.Success())
                    } else {
                        _loginEventFlow.emit(Event.NotInitalized(response.data!!.seq))
                    }
                }.onFailure { response ->
                    if (response.data == null) {
                        ResultType.Fail("다시 시도해주세요.")
                        return@collectLatest
                    }
                }.onError {exception ->
                    Firebase.crashlytics.recordException(exception)
                    _loginEventFlow.emit(Event.Fail("서버 에러입니다. 다시 시도해주세요."))
                }
            }
        }

    }

    fun checkAutoLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            getJWTDataStoreUseCase().collectLatest {
                if (it.isNotBlank()) {
                    _loginEventFlow.emit(Event.Success())
                }
            }
        }
    }

    fun savePermissionCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            savePermissionCheckUseCase(true)
        }
    }

    fun getPermissionCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            getPermissionCheckUseCase().collectLatest {
                _permissionEventFlow.emit(it)
            }
        }
    }

    sealed class Event {
        class Success() : Event()
        data class Fail(val message: String) : Event()

        data class NotInitalized(val seq: Long) : Event()
    }
}
