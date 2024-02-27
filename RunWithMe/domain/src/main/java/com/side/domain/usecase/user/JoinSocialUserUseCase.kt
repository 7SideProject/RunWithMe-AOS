package com.side.domain.usecase.user

import com.side.domain.model.Profile
import com.side.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinSocialUserUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    operator fun invoke(userSeq: Long, profile: Profile) = userRepository.joinSocialUser(userSeq, profile)
}