package com.side.runwithme.view.challenge.create

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeCreate3Binding
import com.side.runwithme.util.GOAL_TYPE
import com.side.runwithme.view.challenge.create.dialog.GoalDaysDialog
import com.side.runwithme.view.challenge.create.dialog.GoalDaysDialogListener
import com.side.runwithme.view.challenge.create.dialog.GoalTypeDistanceDialog
import com.side.runwithme.view.challenge.create.dialog.GoalTypeDistanceDialogListener
import com.side.runwithme.view.challenge.create.dialog.GoalTypeTimeDialog
import com.side.runwithme.view.challenge.create.dialog.GoalTypeTimeDialogListener

class ChallengeCreate3Fragment : BaseFragment<FragmentChallengeCreate3Binding>(R.layout.fragment_challenge_create3) {

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
                findNavController().navigate(R.id.action_challengeCreate3Fragment_to_challengeCreate4Fragment)
            }

            btnCreateGoalAmount.setOnClickListener {
                if(challengeCreateViewModel.goalType.value == GOAL_TYPE.TIME.type){
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
        val goalTypeDistanceDialog = GoalTypeDistanceDialog(goalTypeDistanceDialogListener)
        goalTypeDistanceDialog.show(childFragmentManager, "GoalTypeDistanceDialog")
    }

    private fun initGoalTypeTimeDialog() {
        val goalTypeTimeDialog = GoalTypeTimeDialog(goalTypeTimeDialogListener)
        goalTypeTimeDialog.show(childFragmentManager, "GoalTypeTimeDialog")
    }

    private val goalTypeTimeDialogListener : GoalTypeTimeDialogListener = object :
        GoalTypeTimeDialogListener {
        override fun onItemClick(time: Int) {
            challengeCreateViewModel.setGoalTime(time)
        }
    }

    private val goalTypeDistanceDialogListener : GoalTypeDistanceDialogListener = object :
        GoalTypeDistanceDialogListener {
        override fun onItemClick(distance: Int) {
            challengeCreateViewModel.setGoalDistance(distance)
        }
    }

    private fun initGoalDaysDialog() {
        val goalDaysDialog = GoalDaysDialog(goalDaysDialogListener)
        goalDaysDialog.show(childFragmentManager, "GoalDaysDialog")
    }

    private val goalDaysDialogListener : GoalDaysDialogListener = object : GoalDaysDialogListener {
        override fun onItemClick(days: Int) {
            challengeCreateViewModel.setDays(days)
        }
    }
}