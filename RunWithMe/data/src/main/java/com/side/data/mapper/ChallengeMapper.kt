package com.side.data.mapper

import com.side.data.model.request.CreateChallengeRequest
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
        timeStart = this.timeStart,
        timeEnd = this.timeEnd,
        maxMember = this.maxMember,
        cost = this.cost,
        password = this.password

    )
}


fun Challenge.mapperToChallengeRequest(): CreateChallengeRequest = this.run {
    CreateChallengeRequest(
        name = this.name,
        description = this.description,
        goalDays = this.goalDays,
        goalType = this.goalType,
        goalAmount = this.goalAmount,
        timeStart = this.timeStart,
        timeEnd = this.timeEnd,
        password = this.password,
        cost = this.cost,
        maxMember = this.maxMember
    )
}


