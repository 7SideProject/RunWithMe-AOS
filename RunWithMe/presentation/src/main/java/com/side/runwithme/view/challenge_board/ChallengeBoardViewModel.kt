package com.side.runwithme.view.challenge_board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.usecase.datastore.GetUserSeqDataStoreUseCase
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeBoardViewModel @Inject constructor(
    private val getUserSeqDataStoreUseCase: GetUserSeqDataStoreUseCase
): ViewModel() {

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
                    _getUserSeqEventFlow.emit(true)
                }
            }
        }
    }

}