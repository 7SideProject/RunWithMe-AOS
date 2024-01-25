package com.side.runwithme.view.create_board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.usecase.challenge.CreateBoardUseCase
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CreateBoardViewModel @Inject constructor(
    private val createBoardUseCase: CreateBoardUseCase
) : ViewModel() {

    private val challengeSeq = MutableStateFlow(0L)

    val content = MutableStateFlow<String>("")

    private val _createEventFlow = MutableEventFlow<Event>()
    val createEventFlow = _createEventFlow.asEventFlow()

    fun setChallengeSeq(challengeSeq: Long){
        this.challengeSeq.value = challengeSeq
    }

    fun createBoard(img: MultipartBody.Part? = null){
        if(content.value.isBlank()){
            viewModelScope.launch {
                _createEventFlow.emit(Event.Fail("내용을 입력해주세요."))
            }
            return
        }

        viewModelScope.launch {
            createBoardUseCase(challengeSeq.value, content.value, img).collect {
                it.onSuccess {
                    _createEventFlow.emit(Event.Success())
                }.onFailure {
                    _createEventFlow.emit(Event.Fail("다시 시도해주세요."))
                }.onError {
                    Firebase.crashlytics.recordException(it)
                }
            }
        }
    }

    sealed interface Event {
        class Success : Event
        data class Fail(val message: String) : Event
    }

}