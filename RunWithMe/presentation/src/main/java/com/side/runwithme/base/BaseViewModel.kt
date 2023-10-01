package com.side.runwithme.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseViewModel: ViewModel() {

    protected fun viewModelScopeLaunchIO(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch(Dispatchers.IO, block = block)
    }

    protected fun viewModelScopeLaunchMain(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch(block = block)
    }

}