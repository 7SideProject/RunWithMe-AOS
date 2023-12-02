package com.side.runwithme.view.my_page

import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.data.util.getDecryptStringValue
import com.side.data.util.preferencesKeys
import com.side.domain.model.Profile
import com.side.domain.model.TotalRecord
import com.side.domain.model.User
import com.side.domain.usecase.datastore.GetJWTDataStoreUseCase
import com.side.domain.usecase.datastore.GetUserSeqDataStoreUseCase
import com.side.domain.usecase.user.EditProfileImageUseCase
import com.side.domain.usecase.user.EditProfileUseCase
import com.side.domain.usecase.user.GetTotalRecordUseCase
import com.side.domain.usecase.user.GetUserProfileUseCase
import com.side.runwithme.R
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import com.side.runwithme.util.matchesNickNameRule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserSeqDataStoreUseCase: GetUserSeqDataStoreUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getTotalRecordUseCase: GetTotalRecordUseCase,
    private val editProfileUseCase: EditProfileUseCase,
    private val dataStore: DataStore<Preferences>,
    private val editProfileImageUseCase: EditProfileImageUseCase
): ViewModel() {

    private val _userProfile = MutableStateFlow<User>(User("",""))
    val userProfile get() = _userProfile.asStateFlow()

    private val _totalRecord = MutableStateFlow<TotalRecord>(TotalRecord())
    val totalRecord get() = _totalRecord.asStateFlow()

    val editNickname = MutableStateFlow<String>("")
    val editHeight = MutableStateFlow<Int>(150)
    val editWeight = MutableStateFlow<Int>(50)

    private val _profileImg = MutableStateFlow<MultipartBody.Part?>(null)
    val profileImg = _profileImg.asStateFlow()

    private val _profileImgUri = MutableStateFlow<Uri?>(null)
    val profileImgUri = _profileImgUri.asStateFlow()

    private val _jwt = MutableStateFlow<String>("")
    val jwt = _jwt.asStateFlow()

    private val _editEventFlow = MutableEventFlow<EditEvent>()
    val editEventFlow get() = _editEventFlow.asEventFlow()

    fun myPageInitRequest(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserSeqDataStoreUseCase().collectLatest {
                it.onSuccess { userSeq ->
                    getUserProfile(userSeq)
                    getTotalRecord(userSeq)
                }.onFailure { error->

                }.onError { error ->

                }
            }
        }
    }
    private fun getUserProfile(userSeq: Long){
        viewModelScope.launch(Dispatchers.IO) {
            getUserProfileUseCase(userSeq).collectLatest {
                it.onSuccess { success ->
                    val user = success.data!!
                    _userProfile.value = user

                    user.run {
                        editNickname.value = nickname
                        editWeight.value = weight
                        editHeight.value = height
                    }
                }.onFailure { error->

                }.onError { error ->

                }
            }
        }
    }

    private fun getTotalRecord(userSeq: Long){
        viewModelScope.launch(Dispatchers.IO) {
            getTotalRecordUseCase(userSeq).collectLatest {
                it.onSuccess { success ->
                    _totalRecord.value = success.data!!
                }.onFailure { error->

                }.onError { error ->

                }
            }
        }
    }

    fun setHeight(height: Int){
        this.editHeight.update { height }
    }

    fun setWeight(weight: Int){
        this.editWeight.update { weight }
    }

    fun setProfileImg(img: MultipartBody.Part?){
        _profileImg.value = img
    }

    fun setProfileImgUri(uri: Uri?){
        _profileImgUri.value = uri
    }

    fun editProfileRequest(){
        nicknameInvalidCheck()
    }
    private fun nicknameInvalidCheck() {
        if(!matchesNickNameRule(editNickname.value)){
            viewModelScope.launch {
                _editEventFlow.emit(EditEvent.Fail(R.string.message_invalid_nickname))
            }
        }else{
            editProfile()
        }
    }

    private fun editProfile(){
        viewModelScope.launch(Dispatchers.IO) {
            editProfileUseCase(
                userProfile.value.seq,
                Profile(editNickname.value, editHeight.value, editWeight.value)
            ).collectLatest {
                it.onSuccess {
                    if(profileImg.value != null){
                        editProfileImage()
                        return@onSuccess
                    }
                    _editEventFlow.emit(EditEvent.Success(R.string.message_edit_profile_success))
                }.onFailure {
                    _editEventFlow.emit(EditEvent.Fail(R.string.message_edit_profile_fail))
                }.onError { error ->
                    _editEventFlow.emit(EditEvent.Error())
                }
            }
        }
    }

    private fun editProfileImage(){
        viewModelScope.launch(Dispatchers.IO) {
            editProfileImageUseCase(userProfile.value.seq, profileImg.value!!).collectLatest {
                it.onSuccess {
                    _editEventFlow.emit(EditEvent.Success(R.string.message_edit_profile_success))
                }.onFailure {
                    _editEventFlow.emit(EditEvent.Fail(R.string.message_edit_profile_fail))
                }.onError {
                    _editEventFlow.emit(EditEvent.Error())
                }
            }
        }
    }

    fun getJwtData(){
        viewModelScope.launch(Dispatchers.IO) {
            val flow = dataStore.getDecryptStringValue(preferencesKeys.JWT)
            flow.onEach {
                _jwt.value = it.toString()
            }.launchIn(viewModelScope)
        }
    }

    sealed class EditEvent {
        data class Success(val message: Int) : EditEvent()
        data class Fail(val message: Int) : EditEvent()
        class Error : EditEvent()
    }
}