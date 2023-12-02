package com.side.runwithme.view.challenge_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.side.domain.model.Challenge
import com.side.domain.usecase.challenge.GetRecruitingChallengeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val getRecruitingChallengeListUseCase: GetRecruitingChallengeListUseCase
) : ViewModel() {

    private val CHALLNEGE_SIZE = 200

    fun getRecruitingChallengesPaging(
    ) : Flow<PagingData<Challenge>> {
        return getRecruitingChallengeListUseCase(CHALLNEGE_SIZE).cachedIn(viewModelScope)
    }

}