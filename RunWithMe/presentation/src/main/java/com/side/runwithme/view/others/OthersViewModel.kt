package com.side.runwithme.view.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.usecase.user.DeleteUserUseCase
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersViewModel @Inject constructor(
    private val deleteUserUseCase: DeleteUserUseCase
): ViewModel() {

    private val _deleteUserEventFlow = MutableEventFlow<DeleteUserEvent>()
    val deleteUserEventFlow = _deleteUserEventFlow.asEventFlow()

    fun deleteUser(){
        viewModelScope.launch(Dispatchers.IO) {
            deleteUserUseCase().collectLatest {
                it.onSuccess {
                    _deleteUserEventFlow.emit(DeleteUserEvent.Success())
                }.onFailure {
                    _deleteUserEventFlow.emit(DeleteUserEvent.Fail())
                }.onError {
                    Firebase.crashlytics.recordException(it)
                }
            }
        }
    }

    sealed class DeleteUserEvent {
        class Success : DeleteUserEvent()
        class Fail : DeleteUserEvent()
    }
}