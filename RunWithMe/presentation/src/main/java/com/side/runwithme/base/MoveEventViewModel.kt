package com.side.runwithme.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.side.runwithme.util.MutableEventFlow
import com.side.runwithme.util.asEventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class MoveEventViewModel<T> : ViewModel() {

    private val _moveScreenEventFlow = MutableEventFlow<T>()
    val moveScreenEventFlow get() = _moveScreenEventFlow.asEventFlow()

    protected fun emitMoveEventFlow(event: T){
        viewModelScope.launch {
            _moveScreenEventFlow.emit(event)
        }
    }

    protected fun viewModelScopeLaunchIO(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch(Dispatchers.IO, block = block)
    }

    protected fun viewModelScopeLaunchMain(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch(block = block)
    }



}

