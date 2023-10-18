package com.side.runwithme.view.running_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.side.domain.model.Challenge
import com.side.domain.usecase.challenge.GetMyChallengeListUseCase
import com.side.domain.usecase.datastore.SaveTTSOptionUseCase
import com.side.domain.utils.onError
import com.side.domain.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningListViewModel @Inject constructor(
    private val saveTTSOptionUseCase: SaveTTSOptionUseCase,
    private val getMyChallengeListUseCase: GetMyChallengeListUseCase
) : ViewModel() {

    private val _myChallenges = MutableStateFlow<PagingData<Challenge>>(PagingData.empty())
    val myChallenges = _myChallenges.asStateFlow()

    private val _ttsClickFlag: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val ttsClickFlag = _ttsClickFlag.asStateFlow()

    fun getMyChallenges() {
        viewModelScope.launch(Dispatchers.IO) {
            getMyChallengeListUseCase(20).collectLatest {
                it.onSuccess {
                    _myChallenges.value = it
                }.onError {

                }
            }
        }
    }

    fun getCoordinates() {

    }

    fun getMyScrap() {

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