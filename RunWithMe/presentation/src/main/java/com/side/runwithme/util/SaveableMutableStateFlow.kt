package com.side.runwithme.util

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow

// 상태 저장을 위한 class
class SaveableMutableStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    initialValue: T
) {

    private val state: StateFlow<T> = savedStateHandle.getStateFlow(key, initialValue)
    var value: T
        get() = state.value
        set(value) {
            savedStateHandle[key] = value
        }

    fun asStateFlow(): StateFlow<T> = state

}

fun <T> SavedStateHandle.getMutableStateFlow(
    key: String,
    initialValue: T
): SaveableMutableStateFlow<T> = SaveableMutableStateFlow(this, key, initialValue)