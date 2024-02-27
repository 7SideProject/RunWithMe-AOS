package com.side.domain.usecase.user

import com.side.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginWithKakaoUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    operator fun invoke(accessToken: String) = userRepository.loginWithKakako(accessToken)
}