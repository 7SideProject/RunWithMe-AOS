package com.side.runwithme.view.challenge_list

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeListBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.challenge_list.create.ChallengeCreateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeListFragment :
    BaseFragment<FragmentChallengeListBinding>(R.layout.fragment_challenge_list) {

    private val challengeViewModel: ChallengeViewModel by viewModels()

    private lateinit var challengeListAdapter: ChallengeListAdapter

    override fun init() {

        binding.challengeVM = challengeViewModel

        initToolbarClickListener()

        initChallengeList()

        initClickListener()

        initViewModelCallback()

    }

    private fun initViewModelCallback() {
        // Coroutine 사용 때문에 binding 못함
        lifecycleScope.launch {
            challengeViewModel.challengeList.collectLatest { challengeList ->

                challengeListAdapter.submitData(challengeList)

            }
        }
    }

    private fun initClickListener() {
        binding.apply {
            fabCreateChallenge.setOnClickListener {
                startActivity(Intent(requireContext(), ChallengeCreateActivity::class.java))
            }
        }
    }

    private fun initToolbarClickListener() {
        binding.apply {
            toolbarChallengeList.setBackButtonClickEvent {
                findNavController().popBackStack()
            }
            toolbarChallengeList.setOptionButtonClickEvent(1) {
                findNavController().navigate(ChallengeListFragmentDirections.actionChallengeListFragmentToChallengeSearchFragment())
            }
        }

    }

    private fun initChallengeList() {
        challengeListAdapter = ChallengeListAdapter()
        binding.rvChallengeList.adapter = challengeListAdapter
        challengeViewModel.getRecruitingChallengesPaging(20)

    }
}