package com.side.runwithme.view.challenge_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.side.domain.model.Challenge
import com.side.domain.usecase.challenge.GetRecruitingChallengeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val getRecruitingChallengeListUseCase: GetRecruitingChallengeListUseCase
) : ViewModel() {


    private val _challengeList =  MutableStateFlow<PagingData<Challenge>>(PagingData.empty())
    val challengeList = _challengeList.asStateFlow()

    fun getRecruitingChallengesPaging(
        size: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getRecruitingChallengeListUseCase(size).collectLatest {
                it.onSuccess {
                    Log.d("test123", "getChallengesPaging: ${it}")
                    _challengeList.value = it
                }.onError {
                    Log.e("test123", "getRecruitingChallengesPaging: ${it.cause}, ${it.message}")
                }

            }
        }


    }

}