package com.side.runwithme.view.challenge.create

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeCreate3Binding
import com.side.runwithme.util.GOAL_TYPE
import com.side.runwithme.view.challenge.create.dialog.GoalDaysDialog
import com.side.runwithme.view.challenge.create.dialog.GoalTypeDistanceDialog
import com.side.runwithme.view.challenge.create.dialog.GoalTypeTimeDialog

class ChallengeCreateStep3Fragment : BaseFragment<FragmentChallengeCreate3Binding>(R.layout.fragment_challenge_create3) {

    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()

    override fun init() {
        binding.apply {
            challengeCreateVM = challengeCreateViewModel
        }

        initClickListener()

        initRadioGroupCallbacks()
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

            btnNext.setOnClickListener {
                findNavController().navigate(ChallengeCreateStep3FragmentDirections.actionChallengeCreateStep3FragmentToChallengeCreateStep4Fragment())
            }

            btnCreateGoalAmount.setOnClickListener {
                if(challengeCreateViewModel.goalType.value == GOAL_TYPE.TIME){
                    initGoalTypeTimeDialog()
                }else{
                    initGoalTypeDistanceDialog()
                }
            }

            btnCreateGoalDays.setOnClickListener {
                initGoalDaysDialog()
            }
        }
    }

    private fun initRadioGroupCallbacks(){
        binding.rgGoal.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_time ->{
                    challengeCreateViewModel.setGoalType(GOAL_TYPE.TIME)
                    challengeCreateViewModel.setGoalAmountByTime()
                }
                R.id.rb_distance ->{
                    challengeCreateViewModel.setGoalType(GOAL_TYPE.DISTANCE)
                    challengeCreateViewModel.setGoalAmountByDistance()
                }
            }
        }
    }

    private fun initGoalTypeDistanceDialog() {
        val goalTypeDistanceDialog = GoalTypeDistanceDialog()
        goalTypeDistanceDialog.show(childFragmentManager, "GoalTypeDistanceDialog")
    }

    private fun initGoalTypeTimeDialog() {
        val goalTypeTimeDialog = GoalTypeTimeDialog()
        goalTypeTimeDialog.show(childFragmentManager, "GoalTypeTimeDialog")
    }


    private fun initGoalDaysDialog() {
        val goalDaysDialog = GoalDaysDialog()
        goalDaysDialog.show(childFragmentManager, "GoalDaysDialog")
    }


}