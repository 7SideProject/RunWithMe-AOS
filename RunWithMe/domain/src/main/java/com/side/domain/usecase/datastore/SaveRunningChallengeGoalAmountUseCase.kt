package com.side.domain.usecase.datastore

import com.side.domain.repository.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveRunningChallengeGoalAmountUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
){
    operator suspend fun invoke(goalAmount: Long) = dataStoreRepository.saveRunningGoalAmount(goalAmount)
}