package com.side.domain.usecase.challenge

import com.side.domain.repository.ChallengeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMyChallengeListUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
){
    operator fun invoke(size: Int) = challengeRepository.getMyChallengeList(size)
}