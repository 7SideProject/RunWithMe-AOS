package com.side.runwithme.view.my_challenge

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.side.domain.model.Challenge
import com.side.domain.usecase.challenge.GetMyChallengeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyChallengeViewModel @Inject constructor(
    private val getMyChallengeListUseCase: GetMyChallengeListUseCase
): ViewModel() {

    private val _myChallenges = MutableStateFlow<PagingData<Challenge>>(PagingData.empty())
    val myChallenges = _myChallenges.asStateFlow()

    fun getMyChallengeList(){
        val size = 20
        viewModelScope.launch(Dispatchers.IO) {
            getMyChallengeListUseCase(size).collectLatest {
                it.onSuccess {
                    _myChallenges.value = it
                    Log.d("test123", "getMyChallengeList: ${it.toString()}")
                }.onError {
                    Log.e("test123", "getMyChallengeList: ", it)
                }
            }
        }
    }

}