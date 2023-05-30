package com.side.domain.usecase.user

import com.side.domain.repository.ChallengeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetChallengeListUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    operator fun invoke(
        page: Int,
        size: Int,
        sort: Array<String>
    ) = challengeRepository.getChallengeList(page, size, sort)
}