package com.side.domain.usecase.running

import com.side.domain.repository.RunningRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsAvailableRunningTodayUseCase @Inject constructor(
    private val runningRepository: RunningRepository
){
    operator fun invoke(challengeSeq: Long) = runningRepository.isAvailableRunningToday(challengeSeq)
}