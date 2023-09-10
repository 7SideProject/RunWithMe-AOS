package com.side.runwithme.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import kotlinx.coroutines.launch

class HomeViewModel(): ViewModel() {

    private val _moveScreenEventFlow = MutableEventFlow<Event>()
    val moveScreenEventFlow get() = _moveScreenEventFlow.asEventFlow()


    fun onClickChallengeList(){
        viewModelScope.launch {
            _moveScreenEventFlow.emit(Event.ChallengeListAction)
        }
    }


    sealed class Event {
        object ChallengeListAction : Event()

    }

}