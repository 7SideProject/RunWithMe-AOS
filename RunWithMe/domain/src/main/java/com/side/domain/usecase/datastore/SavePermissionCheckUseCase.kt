package com.side.domain.usecase.datastore

import com.side.domain.repository.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavePermissionCheckUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
){
    suspend operator fun invoke(isCheck: Boolean) = dataStoreRepository.savePermissionCheck(isCheck)
}