package com.side.runwithme.view.challenge_detail

import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeDetailBinding
import com.side.runwithme.model.ChallengeParcelable
import com.side.runwithme.util.CHALLENGE_STATE
import com.side.runwithme.util.GOAL_TYPE
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.running.RunningActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallengeDetailFragment :
    BaseFragment<FragmentChallengeDetailBinding>(R.layout.fragment_challenge_detail) {

    private val args by navArgs<ChallengeDetailFragmentArgs>()
    private val challengeDetailViewModel by viewModels<ChallengeDetailViewModel>()

    override fun init() {

        val challenge = args.challenge

        require(challenge != null)

        binding.apply {
            challengeDetailVM = challengeDetailViewModel
        }

        initChallenge(challenge)

        initViewModelCallbacks()

        initClickListener()
    }

    private fun initClickListener() {
        binding.apply {
            layoutShowMyRecord.setOnClickListener {
//                findNavController().navigate()
            }
            toolbarMain.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

            btnOk.setOnClickListener {
                when (challengeDetailViewModel.challengeState.value) {
                    CHALLENGE_STATE.NOT_START_AND_ALEADY_JOIN -> {
                        initChallengeDetailDialog(
                            resources.getString(
                                R.string.quit_challenge_content,
                                challengeDetailViewModel.challenge.value!!.cost
                            ), quitChallengeClickListener
                        )
                    }

                    CHALLENGE_STATE.NOT_START_AND_NOT_JOIN -> {
                        initChallengeDetailDialog(
                            resources.getString(
                                R.string.join_challenge_content,
                                challengeDetailViewModel.challenge.value?.cost
                            ), joinChallengeClickListener
                        )
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private val passwordDialog = object : JoinChallengePasswordDialogClickListener {
        override fun onClick(password: String) {
            challengeDetailViewModel.joinChallenge(password)
        }
    }

    private val quitChallengeClickListener = object : ChellengeDetailConfirmDialogClickListener {
        override fun onClick() {
            challengeDetailViewModel.quitChallenge()
        }
    }

    private val joinChallengeClickListener = object : ChellengeDetailConfirmDialogClickListener {
        override fun onClick() {
            if (challengeDetailViewModel.challenge.value!!.password.isNullOrBlank()) {
                challengeDetailViewModel.joinChallenge()
                return
            }
            initCheckPasswordDialog()
        }
    }

    private fun initCheckPasswordDialog(){
        val dialog = JoinChallengePasswordDialog(passwordDialog)
        dialog.show(childFragmentManager, "JoinChallengePasswordDialog")
    }

    private fun initChallengeDetailDialog(
        content: String,
        clickListener: ChellengeDetailConfirmDialogClickListener
    ) {
        val dialog = ChallengeDetailConfirmDialog(content, clickListener)
        dialog.show(childFragmentManager, "ChallengeDetailConfirmDialog")
    }

    private fun initViewModelCallbacks() {
        challengeDetailViewModel.apply {
            isChallengeAlreadyJoin()
        }

        repeatOnStarted {
            challengeDetailViewModel.ChallengeDetailEventFlow.collect {
                when (it) {
                    is ChallengeDetailViewModel.Event.Success -> {
//                        goRunning()
                        moveHomeFragment()
                    }

                    is ChallengeDetailViewModel.Event.DeleteChallenge -> {
                        showToast(resources.getString(R.string.delete_challenge_success))
                        findNavController().popBackStack()
                    }

                    is ChallengeDetailViewModel.Event.Fail -> {
                        // 실패 시 메세지
                    }
                }
            }
        }
    }

//    private fun goRunning() {
//        val intent = Intent(requireContext(), RunningActivity::class.java)
//        intent.apply {
//            val challenge = challengeDetailViewModel.challenge.value!!
//            val goalType =
//                if (challenge.goalType == GOAL_TYPE.DISTANCE.apiName) GOAL_TYPE.DISTANCE.ordinal else GOAL_TYPE.TIME.ordinal
//            putExtra("challengeSeq", challenge.seq)
//            putExtra("goalType", goalType)
//            putExtra("goalAmount", challenge.goalAmount)
//        }
//        startActivity(intent)
//    }

    private fun initChallenge(challenge: ChallengeParcelable) {
        challengeDetailViewModel.setChallnege(challenge)
        challengeDetailViewModel.getJwtData()
    }

    private fun moveHomeFragment() {
        findNavController().navigate(R.id.action_challengeDetailFragment_to_HomeFragment)
    }
}
