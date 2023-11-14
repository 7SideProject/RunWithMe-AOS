package com.side.domain.usecase.user

import com.side.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangePasswordUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    operator fun invoke(email: String, password: String) = userRepository.changePassword(email, password)
}