package com.side.runwithme.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.User
import com.side.domain.usecase.datastore.GetJWTDataStoreUseCase
import com.side.domain.usecase.datastore.GetPermissionCheckUseCase
import com.side.domain.usecase.datastore.SavePermissionCheckUseCase
import com.side.domain.usecase.user.LoginWithEmailUseCase
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
    private val getJWTDataStoreUseCase: GetJWTDataStoreUseCase
) : ViewModel() {

    val email: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")

    private val _loginEventFlow = MutableEventFlow<Event>()
    val loginEventFlow get() = _loginEventFlow.asEventFlow()

    private val _permissionEventFlow = MutableEventFlow<Boolean>()
    val permissionEventFlow = _permissionEventFlow.asEventFlow()

    fun loginWithEmail() {
        val user = if(email.value.isBlank()) User("test@naver.com", "12341234") else User(email.value, password.value)

        viewModelScope.launch(Dispatchers.IO) {
            loginWithEmailUseCase(user).collectLatest {
                when (it) {
                    is ResultType.Success -> {
                        Log.d("test123", "loginWithEmail: Success : ${it.data.message}")
                        _loginEventFlow.emit(Event.Success())
                    }
                    is ResultType.Fail -> {
                        Log.d("test123", "loginWithEmail: fail : code ${it.data.code}, message : ${it.data.message}")
                        _loginEventFlow.emit(Event.Fail(it.data.message))
                    }

                    is ResultType.Error -> {
                        Log.d("test123", "${it.exception.message} ")
                        Log.d("test123", "${it.exception.cause} ")
                    }
                    else -> {}
                }
            }
        }
    }

    fun checkAutoLogin(){
        viewModelScope.launch(Dispatchers.IO) {
            getJWTDataStoreUseCase().collectLatest {
                if(it.isNotBlank()){
                    _loginEventFlow.emit(Event.Success())
                }
            }
        }
    }

    fun savePermissionCheck(){
        viewModelScope.launch(Dispatchers.IO) {
            savePermissionCheckUseCase(true)
        }
    }

    fun getPermissionCheck(){
        viewModelScope.launch(Dispatchers.IO) {
            getPermissionCheckUseCase().collectLatest {
                if(!it){
                    savePermissionCheck()
                    _permissionEventFlow.emit(false)
                }
            }
        }
    }

    sealed class Event {
        class Success() : Event()
        data class Fail(val message: String) : Event()

    }

}
