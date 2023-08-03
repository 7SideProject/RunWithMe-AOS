package com.side.domain.usecase.datastore

import com.side.domain.repository.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserSeqDataStoreUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
){
    operator fun invoke() = dataStoreRepository.getUserSeq()
}