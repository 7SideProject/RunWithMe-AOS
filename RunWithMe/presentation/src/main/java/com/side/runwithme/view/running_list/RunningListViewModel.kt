package com.side.runwithme.view.running_list

import androidx.lifecycle.ViewModel
import com.side.domain.model.Challenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class RunningListViewModel @Inject constructor(

) : ViewModel() {

    private val _myChallengeList: MutableStateFlow<List<Challenge>> = MutableStateFlow(emptyList())
    val myChallengeList get() = _myChallengeList.value

    fun getMyChallenges() {

    }

    fun getCoordinates() {

    }

    fun getMyScrap() {

    }

}