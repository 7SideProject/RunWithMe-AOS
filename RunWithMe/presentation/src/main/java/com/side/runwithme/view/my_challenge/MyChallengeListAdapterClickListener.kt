package com.side.runwithme.view.my_challenge

import com.side.domain.model.Challenge
import com.side.runwithme.model.ChallengeParcelable

interface MyChallengeListAdapterClickListener {
    fun onClick(challenge: Challenge)
}