package com.side.runwithme.view.challenge_records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.side.domain.model.ChallengeRunRecord
import com.side.domain.model.RunRecord
import com.side.domain.usecase.challenge.GetRecordsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChallengeRecordsViewModel @Inject constructor(
    private val getRecordsListUseCase: GetRecordsListUseCase
): ViewModel() {

    private val RECORDS_SIZE = 300

    fun getChallengeRecordsPaging(): Flow<PagingData<ChallengeRunRecord>> {
        return getRecordsListUseCase(RECORDS_SIZE).cachedIn(viewModelScope)
    }

}