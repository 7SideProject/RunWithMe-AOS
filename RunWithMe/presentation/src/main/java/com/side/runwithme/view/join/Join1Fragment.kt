package com.side.runwithme.view.join

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentJoin1Binding
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

//        initViewModelCallbacks()
    }

    private fun initClickListener() {
        var waitTime = 0L
        var verifyDelayTime = 0L

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
//                joinViewModel.checkIdIsDuplicate()
                if (System.currentTimeMillis() - verifyDelayTime >= delayTime) {
                    verifyDelayTime = System.currentTimeMillis()
                    layoutVerify.visibility = View.VISIBLE
                    hideKeyboard(etJoinEmail)
                    lifecycleScope.launch {
                        mailSender.sendSecurityCode(requireContext(), etJoinEmail.text.toString())
                    }
                    showToast(resources.getString(R.string.message_send_mail))
                } else {
                    showToast(resources.getString(R.string.message_delay_send_mail))
                }
            }

            btnVerify.setOnClickListener {
                if(etJoinVerifyNumber.text.toString() == mailSender.getEmailCode()){
                    findNavController().navigate(R.id.action_join1Fragment_to_join2Fragment)
                }else{
                    showToast(resources.getString(R.string.message_not_equal_verify))
                }
            }
        }
    }

    // TODO : 아이디 검증 추후 추가
//    private fun initViewModelCallbacks(){
//        var verifyDelayTime = 0L
//
//        binding.apply {
//            if (System.currentTimeMillis() - verifyDelayTime >= delayTime) {
//                verifyDelayTime = System.currentTimeMillis()
//                layoutVerify.visibility = View.VISIBLE
//                hideKeyboard(etJoinEmail)
//                CoroutineScope(Dispatchers.IO).launch {
//                    mailSender.sendSecurityCode(requireContext(), etJoinEmail.text.toString())
//                }
//                showToast(resources.getString(R.string.message_send_mail))
//            } else {
//                showToast(resources.getString(R.string.message_delay_send_mail))
//            }
//        }
//    }

    private fun hideKeyboard(editText: EditText){
        val manager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(editText.applicationWindowToken, 0)
    }
}