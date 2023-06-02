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
//        val user = User("ab@naver.com", "123")
//        val user = User("abcdef@naver.com", "12341234")
        val user = User(email.value, password.value)

        viewModelScope.launch(Dispatchers.IO) {
            loginWithEmailUseCase(user).collectLatest {
                when (it) {
                    is ResultType.Success -> {
                        _loginEventFlow.emit(Event.Success(it.data.data))
                    }
                    is ResultType.Fail -> {
                        _loginEventFlow.emit(Event.Fail(it.data.message))
                    }

                    is ResultType.Error -> {
                        Log.d("joinError", "${it.exception.message} ")
                        Log.d("joinError", "${it.exception.cause} ")
                    }
                    else -> {}
                }
            }
        }
    }

    sealed class Event {
        data class Success(val data: User) : Event()
        data class Fail(val message: String) : Event()

    }

}
