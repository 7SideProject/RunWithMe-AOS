package com.side.domain.usecase.datastore

import com.side.domain.repository.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveTTSOptionUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
){
    operator suspend fun invoke(option: Boolean) = dataStoreRepository.saveTTSOption(option)
}