package com.side.data.util

import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform

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

inline fun <T> Flow<T>.asResult(crossinline action: suspend (value: T) -> ResultType<T>): Flow<ResultType<T>> = this.transform {
    emit(action(it))
}.onStart {
    emitResultTypeLoading()
}.catch {
    emitResultTypeError(it)
}

inline fun <T, R> Flow<T>.asResultOtherType(crossinline action: suspend (value: T) -> ResultType<R>): Flow<ResultType<R>> = this.transform {
    emit(action(it))
}.onStart {
    emitResultTypeLoading()
}.catch {
    emitResultTypeError(it)
}

// datasource에서 사용하려 했지만 datasource 함수를 suspend로 모두 변경해야해서 생략 (datasource는 변경할만큼 귀찮은 코드가 아닌듯)
//inline suspend fun <T> BaseResponse<T>.asResult(): Flow<BaseResponse<T>> =
//    flow {
//        emit(this@asResult)
//    }
