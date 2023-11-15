package com.side.runwithme.view.running_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.side.domain.model.Challenge
import com.side.domain.usecase.challenge.GetAvailableRunningListUseCase
import com.side.domain.usecase.challenge.GetMyChallengeListUseCase
import com.side.domain.usecase.datastore.GetTTSOptionUseCase
import com.side.domain.usecase.datastore.SaveTTSOptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningListViewModel @Inject constructor(
    private val saveTTSOptionUseCase: SaveTTSOptionUseCase,
    private val getAvailableRunningListUseCase: GetAvailableRunningListUseCase,
    private val getTTSOptionUseCase: GetTTSOptionUseCase
) : ViewModel() {

    private val _ttsClickFlag: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val ttsClickFlag = _ttsClickFlag.asStateFlow()

    private val CHALLENGE_SIZE = 20

    fun getMyChallenges() : Flow<PagingData<Challenge>> {
        return getAvailableRunningListUseCase(CHALLENGE_SIZE).cachedIn(viewModelScope)
    }

    fun getCoordinates() {

    }

    fun getMyScrap() {

    }

    fun getTTSOption(){
        viewModelScope.launch {
            getTTSOptionUseCase().collectLatest {
                _ttsClickFlag.value = it
            }
        }
    }

    fun onClickTTsBtn(){
        _ttsClickFlag.value = !_ttsClickFlag.value
        settingTTSOption()
    }

    private fun settingTTSOption(){
        viewModelScope.launch(Dispatchers.IO) {
            saveTTSOptionUseCase(ttsClickFlag.value)
        }
    }
}