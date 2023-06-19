package com.side.domain.usecase.user

import com.side.domain.repository.ChallengeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetChallengeListUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    operator fun invoke(
        size: Int,

    ) = challengeRepository.getChallengeList(size)
}