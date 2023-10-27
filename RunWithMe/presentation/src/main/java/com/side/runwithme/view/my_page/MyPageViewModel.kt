package com.side.runwithme.view.my_page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.User
import com.side.domain.usecase.datastore.GetUserSeqDataStoreUseCase
import com.side.domain.usecase.user.GetUserProfileUseCase
import com.side.domain.utils.onError
import com.side.domain.utils.onFailure
import com.side.domain.utils.onSuccess
import com.side.runwithme.R
import com.side.runwithme.view.join.JoinViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserSeqDataStoreUseCase: GetUserSeqDataStoreUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
): ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile get() = _userProfile.asStateFlow()

    fun getUserProfileRequest(){
        getUserSeq()
    }
    private fun getUserSeq(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserSeqDataStoreUseCase().collectLatest {
                Log.d("getUserProfileError", "getUserProfile: $it")
                it.onSuccess { userSeq ->
                    getUserProfile(userSeq)
                }.onFailure { error->
                    Log.d("getUserSeqError", "${error} ")
                }.onError { error ->
                    Log.d("getUserSeqError", "${error.message} ")
                }
            }
        }
    }
    private fun getUserProfile(userSeq: Long){
        viewModelScope.launch(Dispatchers.IO) {
            getUserProfileUseCase(userSeq).collectLatest {
                Log.d("getUserProfileError", "getUserProfile: $it")
                it.onSuccess { success ->
                    _userProfile.value = success.data
                }.onFailure { error->
                    Log.d("getUserProfileError", "${error.message} ")
                }.onError { error ->
                    Log.d("getUserProfileError", "${error.message} ")
                }
            }
        }
    }
}