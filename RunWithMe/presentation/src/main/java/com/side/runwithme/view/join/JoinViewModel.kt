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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val joinUseCase: JoinUseCase
) : ViewModel() {

    val email: MutableStateFlow<String> = MutableStateFlow("")

    val password: MutableStateFlow<String> = MutableStateFlow("")

    val nickname: MutableStateFlow<String> = MutableStateFlow("")

    private val _weight = MutableStateFlow<Int>(0)
    val weight get() = _weight.asStateFlow()

    private val _height = MutableStateFlow<Int>(0)
    val height get() = _height.asStateFlow()

    private val _imgFile = MutableStateFlow<MultipartBody.Part?>(null)
    val imgFile get() = _imgFile.asStateFlow()

    private val _joinEventFlow = MutableEventFlow<Event>()
    val joinEventFlow get() = _joinEventFlow.asEventFlow()

    fun setHeight(height: Int){
        this._height.update { height }
    }

    fun setWeight(weight: Int){
        this._weight.update { weight }
    }

    fun setImgFile(imgFile: MultipartBody.Part){
        this._imgFile.update { imgFile }
    }

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


    sealed class Event {
        data class Success(val message: String) : Event()
        data class Fail(val message: String) : Event()

    }


}