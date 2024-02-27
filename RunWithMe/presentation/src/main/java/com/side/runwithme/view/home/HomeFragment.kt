package com.side.runwithme.view.home

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentHomeBinding
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
                        alertDailCheck(it.message)
                    }
                    is HomeViewModel.DailyCheckEvent.Fail ->{
                        alertFailDailyCheck(it.message)
                    }
                }

            }
        }
    }

    private fun alertDailCheck(message : Int){
        AlertDialog.Builder(requireContext())
            .setMessage(resources.getString(message))
            .setPositiveButton(resources.getString(R.string.ok)
            ) { _, _ ->

            }
            .create()
            .show()
    }

    private fun alertFailDailyCheck(message: String){
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(resources.getString(R.string.ok)
            ) { _, _ ->

            }
            .create()
            .show()
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
