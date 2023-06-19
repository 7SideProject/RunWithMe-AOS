package com.side.runwithme.view.challenge

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.side.domain.model.Challenge
import com.side.domain.usecase.user.GetChallengeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val getChallengeListUseCase: GetChallengeListUseCase
) : ViewModel() {

    private val _challengeList =  MutableStateFlow<PagingData<Challenge>>(PagingData.empty())
    val challengeList get() = _challengeList.asStateFlow()

    fun getChallengesPaging(
        size: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getChallengeListUseCase(size).collectLatest {

                _challengeList.value = it
            }
        }


    }

}