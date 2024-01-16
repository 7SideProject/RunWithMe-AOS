package com.side.runwithme.view.challenge_records

import com.side.domain.model.ChallengeRunRecord
import com.side.domain.model.RunRecord

interface ChallengeRecordsClickListener {
    fun onClick(record: ChallengeRunRecord)
}