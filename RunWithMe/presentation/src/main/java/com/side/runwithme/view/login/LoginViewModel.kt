package com.side.runwithme.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.User
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
    private val loginWithEmailUseCase: LoginWithEmailUseCase
) : ViewModel() {

    val email: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")

    private val _loginEventFlow = MutableEventFlow<Event>()
    val loginEventFlow get() = _loginEventFlow.asEventFlow()


    fun loginWithEmail() {
        val user = User("tmp@naver.com", "1234")
//        val user = User(email.value, password.value)

        viewModelScope.launch(Dispatchers.IO) {
            loginWithEmailUseCase(user).collectLatest {
                when (it) {
                    is ResultType.Success -> {
                        _loginEventFlow.emit(Event.Success())
                    }
                    is ResultType.Fail -> {
                        Log.d("test123", "loginWithEmail: fail")
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

    sealed class Event {
        class Success() : Event()
        data class Fail(val message: String) : Event()

    }

}
