package com.side.runwithme.view.challenge_list.create

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeCreate4Binding
import com.side.runwithme.view.challenge_list.create.dialog.CostDialog
import com.side.runwithme.view.challenge_list.create.dialog.MaxMemberDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChallengeCreateStep4Fragment : BaseFragment<FragmentChallengeCreate4Binding>(R.layout.fragment_challenge_create4) {

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
                findNavController().navigate(ChallengeCreateStep4FragmentDirections.actionChallengeCreateStep4FragmentToChallengeCreateStep5Fragment())
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
        val costDialog = CostDialog()
        costDialog.show(childFragmentManager, "CostDialog")
    }

    private fun initMaxMemberDialog() {
        val maxMemberDialog = MaxMemberDialog()
        maxMemberDialog.show(childFragmentManager, "MaxMemberDialog")
    }


}