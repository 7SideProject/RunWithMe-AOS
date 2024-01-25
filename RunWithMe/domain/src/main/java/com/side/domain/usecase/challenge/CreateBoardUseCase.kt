package com.side.domain.usecase.challenge

import com.side.domain.repository.ChallengeRepository
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateBoardUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
){
    operator fun invoke(challengeSeq: Long, content: String, image: MultipartBody.Part?) = challengeRepository.createBoard(challengeSeq, content, image)
}