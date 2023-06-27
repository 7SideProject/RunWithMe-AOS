package com.side.data.util

import com.side.domain.base.BaseResponse
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> FlowCollector<ResultType<BaseResponse<T>>>.emitResultTypeError(throwable: Throwable) {
    this.emit(ResultType.Error(throwable))
}