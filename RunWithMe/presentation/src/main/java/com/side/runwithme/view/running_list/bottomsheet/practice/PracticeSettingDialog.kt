package com.side.runwithme.view.running_list.bottomsheet.practice

import com.side.runwithme.R
import com.side.runwithme.base.BaseDialogFragment
import com.side.runwithme.databinding.DialogPracticeSettingBinding
import com.side.runwithme.util.GOAL_TYPE

class PracticeSettingDialog(private val listener: PracticeSettingClickListener) :
    BaseDialogFragment<DialogPracticeSettingBinding>(R.layout.dialog_practice_setting) {

    override fun init() {

        initClickListener()

    }

    private fun initClickListener() {
        var currentType = GOAL_TYPE.DISTANCE

        binding.apply {
            btnUp.setOnClickListener {
                val currentValue = tvGoalAmount.text.toString().toInt()
                if (currentType == GOAL_TYPE.TIME) {
                    val timeMax = 600
                    tvGoalAmount.text = (currentValue + 10).coerceAtMost(timeMax).toString()
                } else if (currentType == GOAL_TYPE.DISTANCE) {
                    val distanceMax = 60
                    tvGoalAmount.text = (currentValue + 1).coerceAtMost(distanceMax).toString()
                }
            }
            btnDown.setOnClickListener {
                val currentValue = tvGoalAmount.text.toString().toInt()
                if (currentType == GOAL_TYPE.TIME && currentValue > 10) {
                    tvGoalAmount.text = (currentValue - 10).toString()
                } else if (currentType == GOAL_TYPE.DISTANCE && currentValue > 1) {
                    tvGoalAmount.text = (currentValue - 1).toString()
                }
            }
            radioGroupPurpose.setOnCheckedChangeListener { _, checkId ->
                when (checkId) {
                    R.id.rb_time -> {
                        tvGoalAmount.text = "30"
                        tvGoalType.text = "분"
                        currentType = GOAL_TYPE.TIME
                    }

                    R.id.rb_distance -> {
                        tvGoalAmount.text = "3"
                        tvGoalType.text = "km"
                        currentType = GOAL_TYPE.DISTANCE
                    }
                }
            }
            btnOk.setOnClickListener {
                var currentValue = tvGoalAmount.text.toString().toLong()
                if (currentType == GOAL_TYPE.TIME) {
                    currentValue *= 60
                } else {
                    currentValue *= 1000
                }
                listener.onItemClick(currentType, currentValue)
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }


}