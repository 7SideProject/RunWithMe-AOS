package com.side.domain.usecase.practice

import com.side.domain.modedl.PracticeRunRecord
import com.side.domain.repository.PracticeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletePracticeRunRecordUseCase @Inject constructor(
    private val practiceRepository: PracticeRepository
){
    operator suspend fun invoke(run: PracticeRunRecord) = practiceRepository.deleteRunRecord(run)
}