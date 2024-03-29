package com.side.domain.usecase.challenge

import com.side.domain.repository.ChallengeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportBoardUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
){
    operator fun invoke(boardSeq: Long) = challengeRepository.reportBoard(boardSeq)
}