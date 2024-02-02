package com.side.runwithme.view.challenge_board

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.data.util.getDecryptStringValue
import com.side.data.util.preferencesKeys
import com.side.domain.model.Board
import com.side.domain.usecase.challenge.DeleteChallengeBoardUseCase
import com.side.domain.usecase.challenge.GetChallengeBoardsUseCase
import com.side.domain.usecase.datastore.GetUserSeqDataStoreUseCase
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeBoardViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val getUserSeqDataStoreUseCase: GetUserSeqDataStoreUseCase,
    private val getChallengeBoardsUseCase: GetChallengeBoardsUseCase,
    private val deleteChallengeBoardUseCase: DeleteChallengeBoardUseCase
): ViewModel() {

    private val _jwt = MutableStateFlow<String>("")
    val jwt = _jwt.asStateFlow()

    private val _challengeSeq = MutableStateFlow<Long>(0L)
    val challengeSeq = _challengeSeq.asStateFlow()

    private val _getUserSeqEventFlow = MutableEventFlow<Boolean>()
    val getUserSeqEventFlow = _getUserSeqEventFlow.asEventFlow()

    private val _boardEventFlow = MutableEventFlow<Event>()
    val boardEventFlow = _boardEventFlow.asEventFlow()

    fun setChallengeSeq(challengeSeq: Long){
        _challengeSeq.value = challengeSeq
    }

    private val _userSeq = MutableStateFlow<Long>(0L)
    val userSeq = _userSeq.asStateFlow()

    fun getUserSeq(){
        viewModelScope.launch {
            getUserSeqDataStoreUseCase().collectLatest {
                it.onSuccess {
                    _userSeq.value = it
                    getJwtData()
                }
            }
        }
    }

    fun getJwtData() {
        viewModelScope.launch(Dispatchers.IO) {
            val flow = dataStore.getDecryptStringValue(preferencesKeys.JWT)
            flow.onEach {
                _jwt.value = it.toString()
                _getUserSeqEventFlow.emit(true)
            }.launchIn(viewModelScope)
        }
    }

    private val BOARDS_SIZE = 200
    fun getBoards() : Flow<PagingData<Board>> {
        return getChallengeBoardsUseCase(challengeSeq.value, BOARDS_SIZE).cachedIn(viewModelScope)
    }

    fun deleteBoard(boardSeq: Long){
        viewModelScope.launch {
            deleteChallengeBoardUseCase(boardSeq).collectLatest {
                it.onSuccess {
                    _boardEventFlow.emit(Event.DeleteBoard())
                }.onFailure {
                    _boardEventFlow.emit(Event.Fail("게시글 삭제에 실패했습니다. 다시 시도해주세요."))
                }.onError {
                    _boardEventFlow.emit(Event.Fail("서버 에러 입니다. 다시 시도해주세요."))
                    Firebase.crashlytics.recordException(it)
                }
            }
        }
    }

    sealed interface Event {
        class DeleteBoard : Event
        data class Fail(val message: String) : Event
        class ReportBoard : Event
    }
}