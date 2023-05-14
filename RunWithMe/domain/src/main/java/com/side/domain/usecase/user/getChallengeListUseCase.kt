package com.side.domain.usecase.user

import com.side.domain.repository.ChallengeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class getChallengeListUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    operator fun invoke() = challengeRepository.getChallengeList()
}