package com.side.runwithme.view.challenge.create

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeCreate4Binding
import com.side.runwithme.view.challenge.create.dialog.CostDialog
import com.side.runwithme.view.challenge.create.dialog.CostDialogListener
import com.side.runwithme.view.challenge.create.dialog.MaxMemberDialog
import com.side.runwithme.view.challenge.create.dialog.MaxMemberDialogListener

class ChallengeCreate4Fragment : BaseFragment<FragmentChallengeCreate4Binding>(R.layout.fragment_challenge_create4) {

    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()


    override fun init() {

        binding.apply {
            challengeCreateVM = challengeCreateViewModel
        }

        initClickListener()

    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_challengeCreate4Fragment_to_challengeCreate5Fragment)
            }

            btnCreateChallengeMaxMember.setOnClickListener {
                initMaxMemberDialog()
            }

            btnCreateChallengeCost.setOnClickListener {
                initCostDialog()
            }
        }
    }

    private fun initCostDialog() {
        val costDialog = CostDialog(costDialogListener)
        costDialog.show(childFragmentManager, "CostDialog")
    }

    private fun initMaxMemberDialog() {
        val maxMemberDialog = MaxMemberDialog(maxMemberDialogListener)
        maxMemberDialog.show(childFragmentManager, "MaxMemberDialog")
    }

    private val maxMemberDialogListener: MaxMemberDialogListener = object : MaxMemberDialogListener {
        override fun onItemClick(max: Int) {
            challengeCreateViewModel.setMaxMember(max.toString())
        }
    }

    private val costDialogListener : CostDialogListener = object : CostDialogListener {
        override fun onItemClick(cost: String) {
            challengeCreateViewModel.setCost(cost)
        }
    }

}