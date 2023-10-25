package com.side.domain.usecase.user

import com.side.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckNicknameIsDuplicateUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(nickname: String) = userRepository.checkNicknameIsDuplicate(nickname)
}