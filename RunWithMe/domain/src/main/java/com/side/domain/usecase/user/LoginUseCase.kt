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
    //fun execute(accessToken: String) = userRepository.loginUser(accessToken)

    operator fun invoke(accessToken: String) = userRepository.loginUser(accessToken)
}