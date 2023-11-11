package com.side.runwithme.view.running_list

import com.side.domain.model.Challenge

interface IntentToRunningActivityClickListener {
    fun onItemClick(challenge: Challenge)
}