package com.side.runwithme.view.join

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentJoin3Binding
import com.side.runwithme.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest

class Join3Fragment : BaseFragment<FragmentJoin3Binding>(R.layout.fragment_join3) {

    private val joinViewModel by activityViewModels<JoinViewModel>()


    override fun init() {
        binding.apply {
            joinVM = joinViewModel
        }

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
            joinViewModel.join3EventFlow.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(joinEvent: JoinViewModel.JoinEvent) {
        when (joinEvent) {
            is JoinViewModel.JoinEvent.Success -> {
                showToast(resources.getString(joinEvent.message))
                requireActivity().finish()
            }

            is JoinViewModel.JoinEvent.Check -> {
                joinViewModel.join()
            }

            is JoinViewModel.JoinEvent.Fail -> {
                showToast(resources.getString(joinEvent.message))
            }
            is JoinViewModel.JoinEvent.Error -> {
                showToast("서버 에러입니다. 다시 시도해주세요.")
            }
        }
    }
}