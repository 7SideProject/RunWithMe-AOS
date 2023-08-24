package com.side.runwithme.view.challenge

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeListBinding
import com.side.runwithme.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeListFragment :
    BaseFragment<FragmentChallengeListBinding>(R.layout.fragment_challenge_list) {

    private val challengeViewModel: ChallengeViewModel by viewModels()

//    private lateinit var challengeListAdapter: ChallengeListAdapter

    override fun init() {

        binding.challengeVM = challengeViewModel
        initToolbarClickListener()
//        initChallengeList()
//        initViewModelCallback()
        initClickListener()
    }

//    private fun initViewModelCallback() {
//        repeatOnStarted {
//            challengeViewModel.challengeList.collectLatest { challengeList ->
//
//                challengeListAdapter.submitData(challengeList)
//
//            }
//        }
//    }

    private fun initClickListener() {
        binding.apply {
            fabCreateChallenge.setOnClickListener {
                findNavController().navigate(R.id.action_challengeListFragment_to_challengeCreate1Fragment)
            }
        }
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

//    private fun initChallengeList() {
//        challengeListAdapter = ChallengeListAdapter()
//        binding.rvChallengeList.adapter = challengeListAdapter
//        challengeViewModel.getChallengesPaging(10)
//
//    }
}