package com.side.runwithme.view.find_password

import androidx.lifecycle.ViewModel
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import kotlinx.coroutines.flow.MutableStateFlow

class FindPasswordViewModel : ViewModel() {

    val id: MutableStateFlow<String> = MutableStateFlow("")

    val password: MutableStateFlow<String> = MutableStateFlow("")

    val passwordConfirm: MutableStateFlow<String> = MutableStateFlow("")

    val verifyNumber = MutableStateFlow<String>("")

    private val _findPasswordEventFlow = MutableEventFlow<Event>()
    val findPasswordEventFlow = _findPasswordEventFlow.asEventFlow()

    sealed class Event {
        data class Success(val message: String) : Event()
        data class Fail(val message: String) : Event()
    }
}