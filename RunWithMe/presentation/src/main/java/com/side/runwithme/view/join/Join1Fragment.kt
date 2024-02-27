package com.side.runwithme.view.join

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentJoin1Binding
import com.side.runwithme.util.repeatOnStarted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class Join1Fragment : BaseFragment<FragmentJoin1Binding>(R.layout.fragment_join1) {

    private val joinViewModel by activityViewModels<JoinViewModel>()
    private val mailSender = SendMail()
    private val delayTime = 30 * 1000L

    override fun init() {

        binding.apply {
            joinVM = joinViewModel
        }

        initClickListener()

        initViewModelCallbacks()
    }

    private fun initClickListener() {
        var waitTime = 0L

        binding.apply {
            toolbar.setBackButtonClickEvent {
                if(System.currentTimeMillis() - waitTime >= 1500){
                    waitTime = System.currentTimeMillis()
                    showToast(resources.getString(R.string.message_back_btn))
                }else {
                    requireActivity().finish()
                }
            }

            btnSendNumber.setOnClickListener {
                sendNumber()
            }

            btnVerify.setOnClickListener {
                verifyNumber()
            }

            etJoinEmail.setOnEditorActionListener { textView, actionId, keyEvent ->
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || keyEvent.action == KeyEvent.KEYCODE_ENTER){
                    sendNumber()
                    true
                } else {
                    false
                }
            }

            etJoinVerifyNumber.setOnEditorActionListener { textView, actionId, keyEvent ->
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || keyEvent.action == KeyEvent.KEYCODE_ENTER){
                    verifyNumber()
                    true
                } else {
                    false
                }
            }
        }
    }

    private var verifyDelayTime = 0L
    private fun sendNumber() {
        binding.run {
            if (System.currentTimeMillis() - verifyDelayTime >= delayTime) {
                verifyDelayTime = System.currentTimeMillis()
                layoutVerify.visibility = View.VISIBLE
                hideKeyboard(etJoinEmail)
                lifecycleScope.launch(Dispatchers.IO) {
                    mailSender.sendSecurityCode(requireContext(), etJoinEmail.text.toString())
                }
                showToast(resources.getString(R.string.message_send_mail))
            } else {
                showToast(resources.getString(R.string.message_delay_send_mail))
            }
        }
    }

    private fun verifyNumber(){
        binding.run {
            if(etJoinVerifyNumber.text.toString() == mailSender.getEmailCode()){
                joinViewModel.checkIdIsDuplicate()
            }else{
                showToast(resources.getString(R.string.message_not_equal_verify))
            }
        }
    }

    private fun initViewModelCallbacks(){
        repeatOnStarted {
            joinViewModel.idIsDuplicateEventFlow.collectLatest {
                    when(it){
                        is JoinViewModel.IdCheckEvent.Success -> {
                            findNavController().navigate(R.id.action_join1Fragment_to_join2Fragment)
                        }
                        is JoinViewModel.IdCheckEvent.Fail-> {
                            showToast(resources.getString(it.message))
                        }
                    }
                }
        }
    }

    private fun hideKeyboard(editText: EditText){
        val manager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(editText.applicationWindowToken, 0)
    }
}