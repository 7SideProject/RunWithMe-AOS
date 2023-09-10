package com.side.runwithme.view.home

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.domain.exception.BearerException
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentHomeBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.home.HomeViewModel.Event.*
import kotlinx.coroutines.flow.collectLatest


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun init() {

        initClickListener()

        initViewModelCallbacks()
    }

    private fun initViewModelCallbacks() {

        repeatOnStarted {
            homeViewModel.moveScreenEventFlow.collectLatest { event ->
                when (event) {
                    ChallengeListAction -> {
                        moveChallengeList()
                    }

                }
            }
        }

    }

    private fun moveChallengeList(){
        findNavController().navigate(R.id.action_homeFragment_to_challengeListFragment)
    }

    private fun initClickListener() {
        binding.apply {
            cvChallenge.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_challengeListFragment)
            }
            ivMyRunning.setOnClickListener {
                throw BearerException()
            }
        }

    }


}
