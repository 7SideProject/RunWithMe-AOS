package com.side.domain.usecase.user

import com.side.domain.model.User
import com.side.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(user: User) = userRepository.join(user)
}