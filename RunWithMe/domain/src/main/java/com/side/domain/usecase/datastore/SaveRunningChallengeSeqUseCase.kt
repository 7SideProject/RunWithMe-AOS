package com.side.domain.usecase.datastore

import com.side.domain.repository.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveRunningChallengeSeqUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
){
    operator suspend fun invoke(challengSeq: Long) = dataStoreRepository.saveRunningChallengeSeq(challengSeq)
}