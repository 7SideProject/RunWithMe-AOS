package com.side.runwithme.usecase

import com.side.domain.model.User
import com.side.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FakeLoginWithEmailUsecase @Inject constructor(
    @field:Named("userrepo")
    private val userRepository: UserRepository
) {
    operator fun invoke(user: User) = userRepository.loginWithEmail(user)
}