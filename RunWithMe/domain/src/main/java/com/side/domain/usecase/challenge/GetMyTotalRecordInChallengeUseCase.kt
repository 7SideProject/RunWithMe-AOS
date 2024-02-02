package com.side.domain.usecase.challenge

import com.side.domain.repository.ChallengeRepository
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMyTotalRecordInChallengeUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {
    operator fun invoke(challengeSeq: Long) = challengeRepository.getMyTotalRecordInChallenge(challengeSeq)
}