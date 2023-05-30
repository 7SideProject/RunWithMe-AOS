package com.side.runwithme.view.challenge

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.domain.base.BaseResponse
import com.side.domain.model.Challenge
import com.side.domain.usecase.user.GetChallengeListUseCase
import com.side.domain.utils.ResultType
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

    private val _challengeList: MutableStateFlow<ResultType<BaseResponse<List<Challenge>>>> =
        MutableStateFlow(ResultType.Uninitialized)
    val challengeList get() = _challengeList.asStateFlow()

    fun getChallenges(
        page: Int,
        size: Int,
        sort: Array<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("test123", "getChallenges: 1")
            getChallengeListUseCase(page, size, sort).collectLatest {
                Log.d("test123", "getChallenges: 2")
                if (it is ResultType.Success) {
                    Log.d("test123", "S: ${it.data.data}")
                    _challengeList.value = it
                } else if (it is ResultType.Error) {
                    Log.d("test123", "E: ${it.exception.message}")
                    Log.d("test123", "E: ${it.exception.cause}")
                } else if (it is ResultType.Fail) {
                    Log.d("test123", "F: ${it.data.data}")
                }
            }
        }


    }

}