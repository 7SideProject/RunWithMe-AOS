package com.side.domain.usecase.datastore

import com.side.domain.model.User
import com.side.domain.repository.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveUserDataStoreUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
){
    operator suspend fun invoke(user: User) = dataStoreRepository.saveUser(user)
}