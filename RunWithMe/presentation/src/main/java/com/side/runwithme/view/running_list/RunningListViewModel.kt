package com.side.runwithme.view.running_list

import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.side.data.util.getDecryptStringValue
import com.side.data.util.preferencesKeys
import com.side.domain.model.Challenge
import com.side.domain.usecase.challenge.GetAvailableRunningListUseCase
import com.side.domain.usecase.challenge.GetMyChallengeListUseCase
import com.side.domain.usecase.datastore.GetJWTDataStoreUseCase
import com.side.domain.usecase.datastore.GetTTSOptionUseCase
import com.side.domain.usecase.datastore.SaveTTSOptionUseCase
import com.side.domain.usecase.running.IsAvailableRunningTodayUseCase
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningListViewModel @Inject constructor(
    private val saveTTSOptionUseCase: SaveTTSOptionUseCase,
    private val getAvailableRunningListUseCase: GetAvailableRunningListUseCase,
    private val getTTSOptionUseCase: GetTTSOptionUseCase,
    private val dataStore: DataStore<Preferences>,
    private val isAvailableRunningTodayUseCase: IsAvailableRunningTodayUseCase
) : ViewModel() {

    private val _ttsClickFlag: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val ttsClickFlag = _ttsClickFlag.asStateFlow()

    private var _jwt = MutableStateFlow<String>("")
    val jwt = _jwt.asStateFlow()

    private val _runningListEventFlow = MutableEventFlow<RunningListEvent>()
    val runningListEventFlow = _runningListEventFlow.asEventFlow()

    private val CHALLENGE_SIZE = 20

    fun getMyChallenges() : Flow<PagingData<Challenge>> {
        return getAvailableRunningListUseCase(CHALLENGE_SIZE).cachedIn(viewModelScope)
    }

    fun getCoordinates() {

    }

    fun getMyScrap() {

    }

    fun isAvailableRunningToday(challenge: Challenge){
        viewModelScope.launch(Dispatchers.IO) {
            isAvailableRunningTodayUseCase(challenge.seq).collectLatest {
                it.onSuccess {
                    Log.d("test123", "isAvailableRunningToday success: ${it.code}")
                    _runningListEventFlow.emit(RunningListEvent.Success(challenge))
                }.onFailure {
                    Log.d("test123", "isAvailableRunningToday fail: ${it.code}")
                    _runningListEventFlow.emit(RunningListEvent.Fail())
                }.onError {

                    Log.d("test123", "isAvailableRunningToday error: ${it}")
                    _runningListEventFlow.emit(RunningListEvent.Error())
                }
            }
        }
    }

    fun getJwtData(){
        viewModelScope.launch(Dispatchers.IO) {
            val flow = dataStore.getDecryptStringValue(preferencesKeys.JWT)
            flow.onEach {
                _jwt.value = it.toString()
            }.launchIn(viewModelScope)
        }
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

    sealed class RunningListEvent() {
        data class Success(val challenge: Challenge): RunningListEvent()
        class Fail: RunningListEvent()
        class Error: RunningListEvent()
    }
}