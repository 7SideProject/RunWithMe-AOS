package com.side.runwithme.view.running_list.bottomsheet.practice

import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogPracticeSettingBinding
import com.side.runwithme.util.GOAL_TYPE_DISTANCE
import com.side.runwithme.util.GOAL_TYPE_TIME

class PracticeSettingDialog(private val listener: PracticeSettingClickListener): BaseDialogFragment<DialogPracticeSettingBinding>(R.layout.dialog_practice_setting) {

    override fun init() {

        initClickListener()

    }

    private fun initClickListener() {
        var currentType = GOAL_TYPE_DISTANCE

        binding.apply {
            btnUp.setOnClickListener {
                val currentValue = tvGoalAmount.text.toString().toInt()
                if(currentType == GOAL_TYPE_TIME && currentValue < 600){
                    tvGoalAmount.text = (currentValue + 10).toString()
                }else if(currentType == GOAL_TYPE_DISTANCE && currentValue < 60){
                    tvGoalAmount.text = (currentValue + 1).toString()
                }
            }
            btnDown.setOnClickListener {
                val currentValue = tvGoalAmount.text.toString().toInt()
                if(currentType == GOAL_TYPE_TIME && currentValue > 10){
                    tvGoalAmount.text = (currentValue- 10).toString()
                }else if(currentType == GOAL_TYPE_DISTANCE && currentValue > 1){
                    tvGoalAmount.text = (currentValue - 1).toString()
                }
            }
            radioGroupPurpose.setOnCheckedChangeListener { _, checkId ->
                when (checkId) {
                    R.id.radio_btn_time -> {
                        tvGoalAmount.text = "30"
                        tvGoalType.text = "분"
                        currentType = GOAL_TYPE_TIME
                    }
                    R.id.radio_btn_distance -> {
                        tvGoalAmount.text = "3"
                        tvGoalType.text = "km"
                        currentType = GOAL_TYPE_DISTANCE
                    }
                }
            }
            btnOk.setOnClickListener {
                var currentValue = tvGoalAmount.text.toString().toInt()
                if(currentType == GOAL_TYPE_TIME){
                    currentValue *= 60
                }else{
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