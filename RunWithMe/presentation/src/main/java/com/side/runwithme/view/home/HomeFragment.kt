package com.side.runwithme.view.home

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentHomeBinding
import com.side.runwithme.util.PasswordVerificationType
import com.side.runwithme.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun init() {

        initClickListener()

        initViewModelCallbacks()
    }

    private fun initViewModelCallbacks() {

        repeatOnStarted {
            homeViewModel.msgEventFlow.collectLatest { event ->
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

        repeatOnStarted {
            homeViewModel.dailyCheckEvent.collectLatest {
                when(it){
                    is HomeViewModel.DailyCheckEvent.Response ->{
                        showToast(resources.getString(it.message))
                    }
                    is HomeViewModel.DailyCheckEvent.Fail ->{
                        showToast(it.message)
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
            cvChallenge.setOnClickListener {
                homeViewModel.onClickChallengeList()
            }

            cvMyRunning.setOnClickListener {
                homeViewModel.onClickMyChallengeList()
            }

            cvDailyCheck.setOnClickListener {
                homeViewModel.dailyCheckRequest()
            }
        }
    }
}
