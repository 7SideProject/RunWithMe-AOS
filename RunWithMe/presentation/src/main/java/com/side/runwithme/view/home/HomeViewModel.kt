package com.side.runwithme.view.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.runwithme.base.MoveEventViewModel
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(): MoveEventViewModel<HomeViewModel.MoveEvent>() {

//    private val _moveScreenEventFlow = MutableEventFlow<Event>()
//    val moveScreenEventFlow get() = _moveScreenEventFlow.asEventFlow()
//
//
//    fun onClickChallengeList(){
//        viewModelScope.launch {
//            _moveScreenEventFlow.emit(Event.ChallengeListAction)
//        }
//    }

//    private val _flow = MutableStateFlow("")
//    fun dd(){
//        viewModelScopeLaunchMain { _flow.emit("dd") }
//    }

    fun onClickChallengeList(){
        Log.d("test123", "onClickChallengeList: ")
        emitMoveEventFlow(MoveEvent.ChallengeListAction)
    }

    fun onClickMyChallengeList(){
        emitMoveEventFlow(MoveEvent.MyChallengeListAction)
    }


    sealed interface MoveEvent {
        object ChallengeListAction : MoveEvent
        object MyChallengeListAction : MoveEvent

    }

}