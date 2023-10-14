package com.side.runwithme.view.home

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentHomeBinding
import com.side.runwithme.util.repeatOnStarted
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
                Log.d("test123", "initViewModelCallbacks: ${event}")
                when (event) {
                    HomeViewModel.MoveEvent.ChallengeListAction -> {
                        moveChallengeList()
                    }

                    HomeViewModel.MoveEvent.MyChallengeListAction -> {
                        moveMyChallengeList()
                    }
                }
            }
        }

    }

    private fun moveChallengeList(){
        findNavController().navigate(R.id.action_homeFragment_to_challengeListFragment)
    }

    private fun moveMyChallengeList(){
        findNavController().navigate(R.id.action_HomeFragment_to_myChallengeFragment)
    }

    private fun initClickListener() {
        binding.apply {
            /** CardView에는 onClick binding이 작동하지 않는 현상 발생 **/
            cvChallenge.setOnClickListener {
                homeViewModel.onClickChallengeList()
            }

            cvMyRunning.setOnClickListener {
                homeViewModel.onClickMyChallengeList()
            }
        }
    }




}
