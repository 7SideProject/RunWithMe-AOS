package com.side.data.mapper

import com.side.data.model.response.ChallengeResponse
import com.side.domain.model.Challenge

fun ChallengeResponse.mapperToChallenge(): Challenge = this.run {
    Challenge(
        seq = this.seq,
        managerSeq = this.managerSeq,
        name = this.name,
        description = this.description,
        imgSeq = this.imgSeq,
        goalDays = this.goalDays,
        goalType = this.goalType,
        goalAmount = this.goalAmount,
        dateStart = this.dateStart,
        dateEnd = this.dateEnd,
        maxMember = this.maxMember,
        cost = this.cost,
        password = this.password

    )
}




