package com.side.domain.utils

import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> FlowCollector<ResultType<BaseResponse<T>>>.emitResultTypeError(throwable: Throwable) {
    this.emit(ResultType.Error(throwable))
}