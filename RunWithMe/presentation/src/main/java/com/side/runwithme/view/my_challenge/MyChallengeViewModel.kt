package com.side.runwithme.view.my_challenge

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.side.domain.model.Challenge
import com.side.domain.usecase.challenge.GetMyChallengeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyChallengeViewModel @Inject constructor(
    private val getMyChallengeListUseCase: GetMyChallengeListUseCase
): ViewModel() {

    private val CHALLNEGE_SIZE = 20

    fun getMyChallengeList() : Flow<PagingData<Challenge>> {
        return getMyChallengeListUseCase(CHALLNEGE_SIZE).cachedIn(viewModelScope)
    }

}