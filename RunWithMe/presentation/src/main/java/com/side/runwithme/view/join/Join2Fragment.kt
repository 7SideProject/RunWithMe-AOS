package com.side.runwithme.view.join

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentJoin2Binding
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
                password_confirm.collectLatest {
                    if(it.equals(password.value)){
                        completePassword()
                        binding.tvAlertPassword.visibility = View.GONE
                    }else{
                        initCompletePassword()
                        if(password.value.isBlank()) {
                            binding.tvAlertPassword.visibility = View.GONE
                        }else{
                            binding.tvAlertPassword.visibility = View.VISIBLE
                        }
                    }
                }
            }

            repeatOnStarted {
                idConfirmEventFlow.collectLatest {
                    handleEvent(it)
                }
            }
        }
    }

    private fun handleEvent(event: JoinViewModel.Event) {
        when (event) {
            is JoinViewModel.Event.Success -> {
                findNavController().navigate(R.id.action_join2Fragment_to_join3Fragment)
            }
            is JoinViewModel.Event.Fail -> {
                showToast(event.message)
            }
        }
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }
            btnNext.setOnClickListener {
                //이메일 중복 검증
                joinViewModel.checkIdIsDuplicate()
            }
        }
    }
}