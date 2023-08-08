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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val joinUseCase: JoinUseCase
) : ViewModel() {

    val email: MutableStateFlow<String> = MutableStateFlow("")

    val password: MutableStateFlow<String> = MutableStateFlow("")

    val password_confirm: MutableStateFlow<String> = MutableStateFlow("")

    val nickname: MutableStateFlow<String> = MutableStateFlow("")

    private val _weight = MutableStateFlow<Int>(0)
    val weight get() = _weight.asStateFlow()

    private val _height = MutableStateFlow<Int>(0)
    val height get() = _height.asStateFlow()

    val phoneNumber = MutableStateFlow<String>("")

    val verifyNumber = MutableStateFlow<String>("")

    private val _resending = MutableStateFlow<Boolean>(false)
    val resending get() = _resending.asStateFlow()

    private val _complete = MutableStateFlow<Boolean>(false)
    val complete get() = _complete.asStateFlow()

    private val _completePassword = MutableStateFlow<Boolean>(false)
    val completePassword get() = _completePassword.asStateFlow()

    val allDone : StateFlow<Boolean> = combine(height, nickname, weight){ height, nickname, weight ->
            Log.d("test123", "combine: ${height}, ${nickname}, ${weight}")
            height != 0 && weight != 0 && nickname.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

//    private val _imgFile = MutableStateFlow<MultipartBody.Part?>(null)
//    val imgFile get() = _imgFile.asStateFlow()


    private val _emailConfirmEventFlow = MutableEventFlow<Event>()
    val emailConfirmEventFlow get() = _emailConfirmEventFlow.asEventFlow()

    private val _joinEventFlow = MutableEventFlow<Event>()
    val joinEventFlow get() = _joinEventFlow.asEventFlow()

    fun setHeight(height: Int){
        this._height.update { height }
    }

    fun setWeight(weight: Int){
        this._weight.update { weight }
    }

    fun completeVerify(){
        this._complete.value = true
    }

    fun completePassword(){
        _completePassword.value = true
    }

    fun initCompletePassword(){
        _completePassword.value = false
    }

    fun clickSendButton(){
        this._resending.value = true
    }

    fun initSendButton(){
        this._resending.value = false
    }

//    fun setImgFile(imgFile: MultipartBody.Part){
//        this._imgFile.update { imgFile }
//    }

    fun join() {
        val user = User(
            seq = 0L,
            email = email.value,
            password = password.value,
            nickname = nickname.value,
            height = height.value,
            weight = weight.value,
            point = 0,
            profileImgSeq = 0L
        )

        viewModelScope.launch(Dispatchers.IO) {
            joinUseCase(user).collectLatest {
                when (it) {
                    is ResultType.Success -> {
                        _joinEventFlow.emit(Event.Success("회원가입 완료"))
                    }

                    is ResultType.Fail -> {
                        _joinEventFlow.emit(Event.Fail(it.data.message))
                    }

                    is ResultType.Error -> {
                        Log.d("joinError", "${it.exception.message} ")
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun checkEmail(){
        viewModelScope.launch(Dispatchers.IO) {
            _emailConfirmEventFlow.emit(Event.Success("이메일 체크 완료"))
        }
    }

//    fun clear(){
//        this.apply {
//            email.value = ""
//            password.value = ""
//            password_confirm.value = ""
//            nickname.value = ""
//            _weight.value = 0
//            _height.value = 0
//            phoneNumber.value = ""
//            verifyNumber.value = ""
//            _resending.value = false
//            _complete.value = false
//            _completePassword.value = false
//            _allDone.value = false
//        }
//    }


    sealed class Event {
        data class Success(val message: String) : Event()
        data class Fail(val message: String) : Event()

    }


}