package com.side.runwithme.view.running_list.bottomsheet.practice

import com.side.runwithme.util.GOAL_TYPE

interface PracticeSettingClickListener {
    fun onItemClick(type: GOAL_TYPE ,amount: Long)
}