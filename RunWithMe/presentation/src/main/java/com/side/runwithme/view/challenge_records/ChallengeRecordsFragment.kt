package com.side.runwithme.view.challenge_records

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.side.domain.model.ChallengeRunRecord
import com.side.domain.model.RunRecord
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentChallengeRecordsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeRecordsFragment : BaseFragment<FragmentChallengeRecordsBinding>(R.layout.fragment_challenge_records){

    private val challengeRecordsViewModel : ChallengeRecordsViewModel by viewModels()
    private lateinit var challengeRecordsAdpater: ChallengeRecordsAdapter

    override fun init() {

        binding.challengeRecordsVM = challengeRecordsViewModel

        initClickListener()

        initChallengeRecordsAdapter()

        initViewModelCallback()
    }

    private fun initClickListener() {
        binding.apply {
            toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initChallengeRecordsAdapter() {
        challengeRecordsAdpater = ChallengeRecordsAdapter(challengeRecordsClickListener)
        binding.rcvChallengeRecords.adapter = challengeRecordsAdpater
    }

    private val challengeRecordsClickListener = object : ChallengeRecordsClickListener {
        override fun onClick(challengeRunRecord: ChallengeRunRecord) {
            /** TODO 기록 상세 조회 페이지 이동 **/
        }
    }

    private fun initViewModelCallback() {
        lifecycleScope.launch {
            challengeRecordsViewModel.getChallengeRecordsPaging().collectLatest { records ->
                challengeRecordsAdpater.submitData(records)
            }
        }
    }

}