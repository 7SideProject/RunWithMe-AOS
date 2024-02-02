package com.side.runwithme.view.challenge_board

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.side.data.util.getDecryptStringValue
import com.side.data.util.preferencesKeys
import com.side.domain.model.Board
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
    private val getChallengeBoardsUseCase: GetChallengeBoardsUseCase
): ViewModel() {

    private val _jwt = MutableStateFlow<String>("")
    val jwt = _jwt.asStateFlow()

    private val _challengeSeq = MutableStateFlow<Long>(0L)
    val challengeSeq = _challengeSeq.asStateFlow()

    private val _getUserSeqEventFlow = MutableEventFlow<Boolean>()
    val getUserSeqEventFlow = _getUserSeqEventFlow.asEventFlow()

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

}