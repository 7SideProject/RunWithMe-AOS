package com.side.domain.usecase.running

import com.side.domain.model.AllRunRecord
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.domain.repository.RunningRepository
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRunRecordUseCase @Inject constructor(
    private val runningRepository: RunningRepository
) {
    operator fun invoke(
        challengeSeq: Int,
        allRunRecord: AllRunRecord
    ) = runningRepository.postRunRecord(
        challengeSeq = challengeSeq,
        allRunRecord = allRunRecord
    )
}