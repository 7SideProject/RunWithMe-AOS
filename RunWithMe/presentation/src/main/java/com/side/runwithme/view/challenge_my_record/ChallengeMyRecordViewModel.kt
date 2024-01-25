package com.side.runwithme.view.challenge_my_record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.TotalRecord
import com.side.domain.usecase.challenge.GetMyTotalRecordInChallengeUseCase
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeMyRecordViewModel @Inject constructor(
    private val getMyTotalRecordInChallengeUseCase: GetMyTotalRecordInChallengeUseCase
) : ViewModel() {

    private val _totalRecord = MutableStateFlow<TotalRecord>(TotalRecord())
    val totalRecord get() = _totalRecord.asStateFlow()

    private val _errorEventFlow = MutableEventFlow<Boolean>()
    val errorEventFlow = _errorEventFlow.asEventFlow()

    fun getMyTotalRecord(challengeSeq: Long){
        viewModelScope.launch {
            getMyTotalRecordInChallengeUseCase(challengeSeq).collectLatest {
                it.onSuccess {
                    _totalRecord.value = it.data ?: TotalRecord()
                }.onFailure {
                    _errorEventFlow.emit(true)
                }.onError {
                    _errorEventFlow.emit(true)
                }
            }
        }
    }

}