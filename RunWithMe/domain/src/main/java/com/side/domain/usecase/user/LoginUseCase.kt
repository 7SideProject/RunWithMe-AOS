package com.side.domain.usecase.user

import com.side.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
//    operator fun invoke(code: String, state: String) = userRepository.login(code, state)
}