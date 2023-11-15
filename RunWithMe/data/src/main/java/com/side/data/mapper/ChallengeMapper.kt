package com.side.data.mapper

import com.side.data.model.request.ChallengeCreateRequest
import com.side.data.model.response.ChallengeResponse
import com.side.domain.model.Challenge

fun ChallengeResponse.mapperToChallenge(): Challenge = this.run {
    Challenge(
        seq = this.seq,
        managerSeq = this.managerSeq,
        managerName = this.managerName,
        name = this.name,
        description = this.description,
        image = this.imgSeq,
        goalDays = this.goalDays,
        goalType = this.goalType,
        goalAmount = this.goalAmount,
        dateStart = this.dateStart,
        dateEnd = this.dateEnd,
        nowMember = this.nowMember,
        maxMember = this.maxMember,
        cost = this.cost,
        password = this.password
    )
}

fun Challenge.mapperToChallengeCreateRequest(): ChallengeCreateRequest = this.run {
    ChallengeCreateRequest(
        name = this.name,
        description = this.description,
        goalDays = this.goalDays,
        goalType = this.goalType,
        goalAmount = this.goalAmount,
        dateStart = this.dateStart,
        dateEnd = this.dateEnd,
        cost = this.cost,
        maxMember = this.maxMember,
        password = this.password
    )
}


