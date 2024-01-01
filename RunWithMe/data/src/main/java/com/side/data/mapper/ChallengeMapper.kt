package com.side.data.mapper

import com.side.data.model.request.ChallengeCreateRequest
import com.side.data.model.response.ChallengeListResponse
import com.side.data.model.response.ChallengeResponse
import com.side.domain.model.Challenge

fun List<ChallengeListResponse>.mapperToChallengeList(): List<Challenge> = this.run {
    this.map {
        Challenge(
            seq = it.seq,
            managerSeq = it.managerSeq,
            managerName = it.managerName,
            name = it.name,
            description = it.description,
            image = it.image,
            goalDays = it.goalDays,
            goalType = it.goalType,
            goalAmount = it.goalAmount,
            dateStart = it.dateStart,
            dateEnd = it.dateEnd,
            nowMember = it.nowMember,
            maxMember = it.maxMember,
            cost = it.cost,
            password = if(it.passwordIsNull) null else "****"
        )
    }
}

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


