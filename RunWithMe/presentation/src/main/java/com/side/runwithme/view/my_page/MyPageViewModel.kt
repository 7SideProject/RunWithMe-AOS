package com.side.runwithme.view.my_page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.TotalRecord
import com.side.domain.model.User
import com.side.domain.usecase.datastore.GetUserSeqDataStoreUseCase
import com.side.domain.usecase.user.GetTotalRecordUseCase
import com.side.domain.usecase.user.GetUserProfileUseCase
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
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getTotalRecordUseCase: GetTotalRecordUseCase
): ViewModel() {

    private val _userProfile = MutableStateFlow<User>(User("",""))
    val userProfile get() = _userProfile.asStateFlow()

    private val _totalRecord = MutableStateFlow<TotalRecord>(TotalRecord())
    val totalRecord get() = _totalRecord.asStateFlow()

    fun myPageInitRequest(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserSeqDataStoreUseCase().collectLatest {
                Log.d("myPageInitRequest", "getUserProfile: $it")
                it.onSuccess { userSeq ->
                    getUserProfile(userSeq)
                    getTotalRecord(userSeq)
                }.onFailure { error->
                    Log.d("myPageInitRequest", "${error} ")
                }.onError { error ->
                    Log.d("myPageInitRequest", "${error.message} ")
                }
            }
        }
    }
    private fun getUserProfile(userSeq: Long){
        viewModelScope.launch(Dispatchers.IO) {
            getUserProfileUseCase(userSeq).collectLatest {
                Log.d("getUserProfileError", "getUserProfile: $it")
                it.onSuccess { success ->
                    _userProfile.value = success.data!!
                }.onFailure { error->
                    Log.d("getUserProfileError", "${error.message} ")
                }.onError { error ->
                    Log.d("getUserProfileError", "${error.message} ")
                }
            }
        }
    }

    private fun getTotalRecord(userSeq: Long){
        viewModelScope.launch(Dispatchers.IO) {
            getTotalRecordUseCase(userSeq).collectLatest {
                Log.d("getTotalRecordError", "getTotalRecord: $it")
                it.onSuccess { success ->
                    _totalRecord.value = success.data!!
                }.onFailure { error->
                    Log.d("getTotalRecordError", "${error.message} ")
                }.onError { error ->
                    Log.d("getTotalRecordError", "${error.message} ")
                }
            }
        }
    }
}