package com.side.data.util

import com.side.domain.base.BaseResponse
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> FlowCollector<ResultType<T>>.emitResultTypeError(throwable: Throwable) {
    this.emit(ResultType.Error(throwable))
}

suspend fun FlowCollector<ResultType.Loading>.emitResultTypeLoading() {
    this.emit(ResultType.Loading)
}

suspend fun <T> FlowCollector<ResultType<T>>.emitResultTypeSuccess(data: T) {
    this.emit(ResultType.Success(data))
}

suspend fun <T> FlowCollector<ResultType<T>>.emitResultTypeFail(data: T) {
    this.emit(ResultType.Fail(data))
}