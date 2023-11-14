package com.side.runwithme.view.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.model.User
import com.side.domain.usecase.datastore.SaveTokenDataStoreUseCase
import com.side.domain.usecase.datastore.SaveUserDataStoreUseCase
import com.side.domain.usecase.user.DeleteUserUseCase
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersViewModel @Inject constructor(
    private val deleteUserUseCase: DeleteUserUseCase,
    private val saveUserDataStoreUseCase: SaveUserDataStoreUseCase,
    private val saveTokenDataStoreUseCase: SaveTokenDataStoreUseCase
) : ViewModel() {

    private val _deleteUserEventFlow = MutableEventFlow<DeleteUserEvent>()
    val deleteUserEventFlow = _deleteUserEventFlow.asEventFlow()

    private val _logoutEventFlow = MutableEventFlow<LogoutEvent>()
    val logoutEventFlow = _logoutEventFlow.asEventFlow()

    fun deleteUser() {
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

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            saveTokenDataStoreUseCase("", "")
            saveUserDataStoreUseCase(
                User(
                    seq = 0L,
                    email = "",
                    nickname = "",
                    height = 0,
                    weight = 0,
                    point = 0
                )
            )

            delay(500L)

            _logoutEventFlow.emit(LogoutEvent.Success())
        }
    }

    sealed class DeleteUserEvent {
        class Success : DeleteUserEvent()
        class Fail : DeleteUserEvent()
    }

    sealed class LogoutEvent {
        class Success : LogoutEvent()
    }
}