package com.side.runwithme.view.challenge

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallengeListFragment : BaseFragment<FragmentChallengeListBinding>(R.layout.fragment_challenge_list) {

    private val challengeViewModel: ChallengeViewModel by viewModels()

    private lateinit var challengeListAdapter: ChallengeListAdapter

    override fun init() {
        binding.lifecycleOwner = this
        binding.challengeVM = challengeViewModel
        initToolbarClickListener()
        initChallengeList()

    }

    private fun initToolbarClickListener() {
        binding.apply {
            toolbarChallengeList.setBackButtonClickEvent {
                findNavController().popBackStack()
            }
            toolbarChallengeList.setOptionButtonClickEvent(1) {
                findNavController().navigate(R.id.action_challengeListFragment_to_challengeSearchFragment)
            }
        }

    }

    private fun initChallengeList() {
//        challengeViewModel.getChallenges()
        challengeListAdapter = ChallengeListAdapter()
        binding.rvChallengeList.adapter = challengeListAdapter
    }
}