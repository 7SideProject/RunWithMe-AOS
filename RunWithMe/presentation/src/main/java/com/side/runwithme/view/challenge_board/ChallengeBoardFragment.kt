package com.side.runwithme.view.challenge_board

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentChallengeBoardBinding
import com.side.runwithme.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeBoardFragment : BaseFragment<FragmentChallengeBoardBinding>(R.layout.fragment_challenge_board) {

    private val args by navArgs<ChallengeBoardFragmentArgs>()
    private val challengeBoardViewModel : ChallengeBoardViewModel by viewModels()

    override fun init() {

        val challengeSeq = args.challengeSeq

        if(challengeSeq == 0L){
            showToast("잘못된 접근입니다.")
            findNavController().popBackStack()
        }

        challengeBoardViewModel.setChallengeSeq(challengeSeq)

        initClickListener()

        initViewModelCallbacks()
    }

    private fun initClickListener(){
        binding.apply {
            fabCreateBoard.setOnClickListener {
                val action = ChallengeBoardFragmentDirections.actionChallengeBoardFragmentToCreateBoardFragment(challengeBoardViewModel.challengeSeq.value)
                findNavController().navigate(action)
            }
        }
    }

    private fun initViewModelCallbacks(){
        challengeBoardViewModel.apply {
            getUserSeq()

            repeatOnStarted {
                getUserSeqEventFlow.collectLatest {
                    if(it){
                        initChallengeBoardAdapter()
                    }
                }
            }
        }
    }

    private fun initChallengeBoardAdapter(){
        val challengeBoardAdapter = ChallengeBoardAdapter(
            userSeq = challengeBoardViewModel.challengeSeq.value,
            deleteBoardClickListener = deleteBoardClickListener,
            reportBoardClickListener = reportBoardClickListener,
            challengeBoardViewModel.jwt
        )
        binding.rcvChallengeBoard.adapter = challengeBoardAdapter

        lifecycleScope.launch {
            challengeBoardViewModel.getBoards().collectLatest {boards ->
                challengeBoardAdapter.submitData(boards)
            }
        }
    }

    private val deleteBoardClickListener = object : DeleteBoardClickListener {
        override fun onClick(boardSeq: Long) {
            /** TODO delete **/
        }
    }

    private val reportBoardClickListener = object : ReportBoardClickListener {
        override fun onClick(boardSeq: Long) {
            /** TODO report **/
        }
    }

}