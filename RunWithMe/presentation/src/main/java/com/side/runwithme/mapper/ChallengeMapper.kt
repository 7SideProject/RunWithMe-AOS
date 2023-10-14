package com.side.runwithme.mapper

import com.side.domain.model.Challenge
import com.side.runwithme.model.ChallengeParcelable

fun Challenge.mapperToChallengeParcelable(): ChallengeParcelable = this.run {
    return ChallengeParcelable(
        seq = this.seq,
        managerSeq = this.managerSeq,
        managerName = this.managerName,
        name = this.name,
        description = this.description,
        imgSeq = this.imgSeq,
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