package com.side.runwithme.view.find_password

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityFindPasswordBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.join.SendMail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FindPasswordActivity : BaseActivity<ActivityFindPasswordBinding>(R.layout.activity_find_password) {

    private val findPasswordViewModel by viewModels<FindPasswordViewModel>()
    private val delayTime = 30 * 1000L
    private val mailSender = SendMail()

    override fun init() {

        binding.apply {
            findPasswordVM = findPasswordViewModel
        }

        initClickListener()

        initViewModelCallbacks()
    }

    private fun initClickListener(){
        var waitTime = 0L
        var verifyDelayTime = 0L

        binding.apply {
            toolbar.setBackButtonClickEvent {
                if(System.currentTimeMillis() - waitTime >= 1500){
                    waitTime = System.currentTimeMillis()
                    showToast(resources.getString(R.string.message_back_btn))
                }else {
                    finish()
                }
            }

            btnSendNumber.setOnClickListener {
                if (System.currentTimeMillis() - verifyDelayTime >= delayTime) {
                    verifyDelayTime = System.currentTimeMillis()
                    layoutVerify.visibility = View.VISIBLE
                    hideKeyboard(etFindPasswordEmail)
                    lifecycleScope.launch(Dispatchers.IO) {
                        mailSender.sendSecurityCode(this@FindPasswordActivity, etFindPasswordEmail.text.toString())
                    }
                    showToast(resources.getString(R.string.message_send_mail))
                } else {
                    showToast(resources.getString(R.string.message_delay_send_mail))
                }
            }

            btnVerify.setOnClickListener {
                if(etVerifyNumber.text.toString() == mailSender.getEmailCode()){
                    layoutFindPassword.visibility = View.VISIBLE
                    etPassword.requestFocus()
                    showToast(resources.getString(R.string.message_equal_verify))
                    findPasswordViewModel.verifyEmail()
                }else{
                    showToast(resources.getString(R.string.message_not_equal_verify))
                }
            }
        }
    }

    private fun initViewModelCallbacks(){
        findPasswordViewModel.apply {
            repeatOnStarted {
                findPasswordEventFlow.collectLatest {
                    when(it){
                        is FindPasswordViewModel.Event.Success -> {
                            showToast(it.message)
                            finish()
                        }
                        is FindPasswordViewModel.Event.Fail -> {
                            showToast(it.message)
                        }
                    }
                }
            }
        }
    }

    private fun hideKeyboard(editText: EditText){
        val manager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(editText.applicationWindowToken, 0)
    }

}