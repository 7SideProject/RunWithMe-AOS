package com.side.runwithme.view.crew

import androidx.lifecycle.ViewModel
import com.side.domain.model.Crew
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CrewViewModel : ViewModel() {

    private val _crewList: MutableStateFlow<ResultType<List<Crew>>> =
        MutableStateFlow(ResultType.Uninitialized)
    val crewList get() = _crewList.asStateFlow()

    fun getCrews() {

        val list  = listOf<Crew>(
            Crew(1,
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
            Crew(2,
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
            Crew(3,
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
        val a = ResultType.Success<List<Crew>>(list)
        _crewList.value = a

    }

}