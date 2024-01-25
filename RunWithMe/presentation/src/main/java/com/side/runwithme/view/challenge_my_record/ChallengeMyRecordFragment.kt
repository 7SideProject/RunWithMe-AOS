package com.side.runwithme.view.challenge_my_record

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentChallengeMyRecordBinding
import com.side.runwithme.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ChallengeMyRecordFragment : BaseFragment<FragmentChallengeMyRecordBinding>(R.layout.fragment_challenge_my_record) {

    private val challengeMyRecordViewModel by viewModels<ChallengeMyRecordViewModel>()
    private val args by navArgs<ChallengeMyRecordFragmentArgs>()
    private var challengeSeq: Long = 0L

    override fun init() {

        challengeSeq = args.challengeSeq

        if(challengeSeq == 0L){
            showToast("에러 입니다. 화면을 다시 나간 뒤 다시 시도해주세요.")
            findNavController().popBackStack()
        }

        binding.apply {
            challengeMyRecordVM = challengeMyRecordViewModel
        }

        initClickListener()

        initViewModelCallbacks()
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }
        }
    }

    private fun initViewModelCallbacks(){
        challengeMyRecordViewModel.apply {
            getMyTotalRecord(challengeSeq)

            repeatOnStarted {
                errorEventFlow.collectLatest {
                    if(it){
                        showToast("에러 입니다. 화면을 다시 나간 뒤 다시 시도해주세요.")
                    }
                }
            }
        }

    }

}