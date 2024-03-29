package com.side.domain.usecase.challenge

import com.side.domain.repository.ChallengeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsChallengeAlreadyJoinUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
){
    operator fun invoke(challengeSeq: Long) = challengeRepository.isChallengeAlreadyJoin(challengeSeq)
}