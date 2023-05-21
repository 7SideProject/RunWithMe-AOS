package com.side.runwithme.view.join

import android.util.Log
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.User
import com.side.domain.usecase.user.JoinUseCase
import com.side.domain.utils.ResultType
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val joinUseCase: JoinUseCase
) : ViewModel() {


    private val _joinEventFlow = MutableEventFlow<Event>()
    val joinEventFlow get() = _joinEventFlow.asEventFlow()

    fun join(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            joinUseCase(user).collectLatest {
                when (it) {
                    is ResultType.Success -> {
                        Log.d("test123", "join: success")
                        _joinEventFlow.emit(Event.Success("회원가입 완료"))
                    }

                    is ResultType.Fail -> {
                        Log.d("test123", "join: fail")
                        _joinEventFlow.emit(Event.Fail(it.data.message))
                    }

                    is ResultType.Error -> {
                        Log.d("test123", "join: error")
                        Log.d("joinError", "${it.exception.message} ")
                    }
                    else -> {

                    }
                }
            }
        }


    }

    sealed class Event {
        data class Success(val message: String) : Event()
        data class Fail(val message: String) : Event()

    }


}