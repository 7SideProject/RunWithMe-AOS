package com.side.runwithme.view.challenge

import androidx.lifecycle.ViewModel
import com.side.domain.model.Challenge
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChallengeViewModel : ViewModel() {

    private val _challengeList: MutableStateFlow<ResultType<List<Challenge>>> =
        MutableStateFlow(ResultType.Uninitialized)
    val challengeList get() = _challengeList.asStateFlow()

    fun getChallenges() {

        val list  = listOf<Challenge>(
            Challenge(1,
                "크루 1",
                "만든 사람 1",
                "1111-11-11 ~ 1111-11-11",
                "거리",
                "주 1회",
                "1KM",
                10,
                3,
                100
            ),
            Challenge(2,
                "크루 2",
                "만든 사람 2",
                "1111-11-11 ~ 1111-11-11",
                "시간",
                "주 2회",
                "2KM",
                10,
                3,
                100
            ),
            Challenge(3,
                "크루 3",
                "만든 사람 3",
                "1111-11-11 ~ 1111-11-11",
                "거리",
                "주 3회",
                "3KM",
                10,
                3,
                100
            ),
        )
        val a = ResultType.Success<List<Challenge>>(list)
        _challengeList.value = a

    }

}