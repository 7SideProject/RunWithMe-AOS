package com.side.runwithme.view.my_challenge

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.side.domain.model.Challenge
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentMyChallengeBinding
import com.side.runwithme.mapper.mapperToChallengeParcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyChallengeFragment : BaseFragment<FragmentMyChallengeBinding>(R.layout.fragment_my_challenge) {

    private val myChallengeViewModel by viewModels<MyChallengeViewModel>()
    private lateinit var myChallengeListAdapter: MyChallengeListAdapter

    override fun init() {

        myChallengeListAdapter = MyChallengeListAdapter(myChallengeListAdapterClickListener, myChallengeViewModel.jwt)

        binding.apply {
            myChallengeVM = myChallengeViewModel
            rcvMyChallenge.adapter = myChallengeListAdapter
        }

        initViewModelCallbacks()

        initClickListener()

        myChallengeViewModel.getJwtData()
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }
        }
    }

    private fun initViewModelCallbacks(){

        lifecycleScope.launch {
            myChallengeViewModel.getMyChallengeList().collectLatest { challengeList ->
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