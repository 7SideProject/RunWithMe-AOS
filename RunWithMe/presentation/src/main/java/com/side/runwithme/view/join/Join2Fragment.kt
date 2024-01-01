package com.side.runwithme.view.join

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentJoin2Binding
import com.side.runwithme.util.PasswordVerificationType
import com.side.runwithme.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest


class Join2Fragment : BaseFragment<FragmentJoin2Binding>(R.layout.fragment_join2) {

    private val joinViewModel by activityViewModels<JoinViewModel>()

    override fun init() {

        binding.apply {
            joinVM = joinViewModel
        }

        initClickListener()

        initViewModelCallbacks()
    }

    private fun initViewModelCallbacks(){
        joinViewModel.apply {
            repeatOnStarted {
                join2EventFlow.collectLatest {
                    when(it){
                        PasswordVerificationType.NOT_EQUAL_ERROR -> {
                            showToast(resources.getString(R.string.not_equal_password))
                        }
                        PasswordVerificationType.LENGTH_ERROR ->{
                            showToast(resources.getString(R.string.not_length_password))
                        }
                        PasswordVerificationType.SUCCESS ->{
                            findNavController().navigate(R.id.action_join2Fragment_to_join3Fragment)
                        }
                        PasswordVerificationType.NOT_VALID -> {
                            showToast(resources.getString(R.string.not_valid_password))
                        }
                    }
                }
            }
        }
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }
            btnNext.setOnClickListener {
                joinViewModel.clickJoin2NextButton()
            }
        }
    }
}