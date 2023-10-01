package com.side.domain.usecase.datastore

import com.side.domain.repository.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveRunningChallengeGoalTypeUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
){
    operator suspend fun invoke(goalType: Int) = dataStoreRepository.saveRunningGoalType(goalType)
}