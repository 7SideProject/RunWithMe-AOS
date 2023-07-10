package com.side.domain.usecase.practice

import com.side.domain.repository.PracticeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPracticeTotalAvgSpeedUseCase @Inject constructor(
    private val practiceRepository: PracticeRepository
){
    operator fun invoke() = practiceRepository.getTotalAvgSpeed()
}