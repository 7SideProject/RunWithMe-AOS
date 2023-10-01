package com.side.domain.usecase.challenge

import com.side.domain.model.Challenge
import com.side.domain.repository.ChallengeRepository
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateChallengeUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
){
    operator fun invoke(challenge: Challenge, imgFile: MultipartBody.Part?) = challengeRepository.createChallenge(
        challenge = challenge,
        imgFile = imgFile
    )
}