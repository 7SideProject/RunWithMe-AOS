package com.side.runwithme.view.social_join

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentSocialJoinBinding
import com.side.runwithme.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SocialJoinFragment : BaseFragment<FragmentSocialJoinBinding>(R.layout.fragment_social_join) {

    private val joinViewModel by viewModels<SocialJoinViewModel>()
    private val navArgs by navArgs<SocialJoinFragmentArgs>()

    override fun init() {
        binding.apply {
            joinVM = joinViewModel
        }

        val userSeq = navArgs.userSeq

        if(userSeq == 0L){
            showToast("다시 시도해주세요.")
            findNavController().popBackStack()
            return
        }

        joinViewModel.setUserSeq(userSeq)

        initClickListener()

        initSpinner()

        initViewModelCallBack()
    }


    private fun initClickListener() {
        binding.apply {
            btnJoin.setOnClickListener {
                joinViewModel.joinInvalidCheck()
            }
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

        }
    }

    private fun initSpinner() {
        initWeightSpinner()
        initHeightSpinner()
    }

    private fun initWeightSpinner() {
        val weightList = Array(231) { i -> i + 20 }

        binding.spinnerEditWeight.apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                weightList
            )
            setSelection(30)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setWeight(weightList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setWeight(weightList[position])
                }
            }
        }
    }

    private fun initHeightSpinner() {
        val heightList = Array(131) { i -> i + 120 }

        binding.spinnerEditHeight.apply {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                heightList
            )
            setSelection(30)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setHeight(heightList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setHeight(heightList[position])
                }
            }
        }
    }

    private fun initViewModelCallBack() {
        repeatOnStarted {
            joinViewModel.joinEventFlow.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(joinEvent: SocialJoinViewModel.JoinEvent) {
        when (joinEvent) {
            is SocialJoinViewModel.JoinEvent.Success -> {
                showToast(resources.getString(joinEvent.message))
                findNavController().popBackStack()
            }

            is SocialJoinViewModel.JoinEvent.Check -> {
                joinViewModel.join()
            }

            is SocialJoinViewModel.JoinEvent.Fail -> {
                showToast(resources.getString(joinEvent.message))
            }
            is SocialJoinViewModel.JoinEvent.Error -> {
                showToast("서버 에러입니다. 다시 시도해주세요.")
            }
        }
    }


}