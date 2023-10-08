package com.side.runwithme.view.running_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.model.Challenge
import com.side.domain.usecase.datastore.SaveTTSOptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningListViewModel @Inject constructor(
    private val saveTTSOptionUseCase: SaveTTSOptionUseCase
) : ViewModel() {

    private val _myChallengeList: MutableStateFlow<List<Challenge>> = MutableStateFlow(emptyList())
    val myChallengeList get() = _myChallengeList.asStateFlow()

    private val _ttsClickFlag: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val ttsClickFlag = _ttsClickFlag.asStateFlow()

    fun getMyChallenges() {

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