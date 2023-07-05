package com.side.data.repository

import com.side.data.datasource.datastore.DataStoreDataSource
import com.side.data.util.emitResultTypeError
import com.side.data.util.emitResultTypeFail
import com.side.data.util.emitResultTypeLoading
import com.side.data.util.emitResultTypeSuccess
import com.side.domain.model.User
import com.side.domain.repository.DataStoreRepository
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepositoryImpl @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource
): DataStoreRepository{

    override suspend fun saveUser(user: User) = dataStoreDataSource.saveUser(user)

    override fun getUserEmail(): Flow<ResultType<String>> = flow<ResultType<String>> {
        emitResultTypeLoading()
        dataStoreDataSource.getUserEmail().collect {
            if(it.isBlank()){
                emitResultTypeFail(it)
            }else {
                emitResultTypeSuccess(it)
            }
        }
    }.catch {
        emitResultTypeError(it)
    }

    override fun getUserSeq(): Flow<ResultType<Long>> = flow {
        emitResultTypeLoading()
        dataStoreDataSource.getUserSeq().collect {
            if(it.isBlank()){
                emitResultTypeFail(0L)
            }else {
                emitResultTypeSuccess(it.toLong())
            }
        }
    }.catch {
        emitResultTypeError(it)
    }

    override fun getUserWeight(): Flow<ResultType<Int>> = flow {
        emitResultTypeLoading()
        dataStoreDataSource.getUserWeight().collect {
            if(it == 0){
                emitResultTypeFail(0)
            }else {
                emitResultTypeSuccess(it)
            }
        }
    }.catch {
        emitResultTypeError(it)
    }
    override suspend fun saveToken(jwt: String, refreshToken: String) = dataStoreDataSource.saveToken(jwt, refreshToken)

//    override fun getJWT(): Flow<ResultType<String>> = flow {
//        emitResultTypeLoading()
//        dataStoreDataSource.getJWT().collect {
//            emitResultTypeSuccess(it)
//        }
//    }.catch {
//        emitResultTypeError(it)
//    }
//
//    override fun getRefreshToken(): Flow<ResultType<String>> = flow {
//        emitResultTypeLoading()
//        dataStoreDataSource.getRefreshToken().collect {
//            emitResultTypeSuccess(it)
//        }
//    }.catch {
//        emitResultTypeError(it)
//    }
}