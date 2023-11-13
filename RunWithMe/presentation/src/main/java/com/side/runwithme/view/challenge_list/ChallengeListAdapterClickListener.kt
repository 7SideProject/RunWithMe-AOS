package com.side.runwithme.view.challenge_list

import com.side.domain.model.Challenge
import com.side.runwithme.model.ChallengeParcelable

interface ChallengeListAdapterClickListener {
    fun onClick(challenge: Challenge)
}