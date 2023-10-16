package com.side.runwithme.view.my_challenge

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.domain.model.Challenge
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentMyChallengeBinding
import com.side.runwithme.mapper.mapperToChallengeParcelable
import com.side.runwithme.model.ChallengeParcelable
import com.side.runwithme.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MyChallengeFragment : BaseFragment<FragmentMyChallengeBinding>(R.layout.fragment_my_challenge) {

    private val myChallengeViewModel by viewModels<MyChallengeViewModel>()
    private lateinit var myChallengeListAdapter: MyChallengeListAdapter

    override fun init() {
        binding.apply {
            myChallengeVM = myChallengeViewModel
            myChallengeListAdapter = MyChallengeListAdapter(myChallengeListAdapterClickListener)
            rcvMyChallenge.adapter = myChallengeListAdapter
        }

        initViewModelCallbacks()

        initClickListener()
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }
        }
    }

    private fun initViewModelCallbacks(){
        myChallengeViewModel.getMyChallengeList()

        repeatOnStarted {
            myChallengeViewModel.myChallenges.collectLatest { challengeList ->
                myChallengeListAdapter.submitData(challengeList)
            }
        }
    }

    private val myChallengeListAdapterClickListener = object : MyChallengeListAdapterClickListener {
        override fun onClick(challenge: Challenge) {
            val action = MyChallengeFragmentDirections.actionMyChallengeFragmentToChallengeDetailFragment(challenge.mapperToChallengeParcelable())
            findNavController().navigate(action)
        }
    }

}