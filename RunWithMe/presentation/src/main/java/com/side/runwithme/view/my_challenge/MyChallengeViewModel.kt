package com.side.runwithme.view.my_challenge

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
import com.side.domain.usecase.challenge.GetMyChallengeListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyChallengeViewModel @Inject constructor(
    private val getMyChallengeListUseCase: GetMyChallengeListUseCase,
    private val dataStore: DataStore<Preferences>
): ViewModel() {

    private val CHALLNEGE_SIZE = 20

    private val _jwt = MutableStateFlow<String>("")
    val jwt = _jwt.asStateFlow()

    fun getMyChallengeList() : Flow<PagingData<Challenge>> {
        return getMyChallengeListUseCase(CHALLNEGE_SIZE).cachedIn(viewModelScope)
    }
    fun getJwtData(){
        viewModelScope.launch(Dispatchers.IO) {
            val flow = dataStore.getDecryptStringValue(preferencesKeys.JWT)
            flow.onEach {
                _jwt.value = it.toString()
            }.launchIn(viewModelScope)
        }
    }
}