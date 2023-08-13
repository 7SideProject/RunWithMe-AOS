package com.side.runwithme.view.join

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentJoin1Binding
import java.util.concurrent.TimeUnit


class Join1Fragment : BaseFragment<FragmentJoin1Binding>(R.layout.fragment_join1) {

    private lateinit var auth : FirebaseAuth
    private var storedVerificationId = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val joinViewModel by activityViewModels<JoinViewModel>()

    override fun init() {

        auth = Firebase.auth
        auth.setLanguageCode("kr")

        binding.apply {
            joinVM = joinViewModel
        }

        initClickListener()

        observeEditText()
    }

    private fun initClickListener() {
        var waitTime = 0L

        binding.apply {
            toolbar.setBackButtonClickEvent {
                if(System.currentTimeMillis() - waitTime >= 1500){
                    waitTime = System.currentTimeMillis()
                    showToast("뒤로가기 버튼을 한번 더 누르면 회원가입이 종료됩니다.")
                }else {
                    requireActivity().finish()
                }
            }
            btnSendNumber.setOnClickListener {
                val krPhoneNum = "+82" + etJoinPhoneNumber.text.toString().substring(1)
                Log.d("test123", "initClickListener: ${krPhoneNum}")
                if (joinViewModel.resending.value) {
                    resendVerificationCode(
                        krPhoneNum,
                        resendToken
                    )

                } else {
                    sendVerifyMessagePhoneNumber(krPhoneNum)
                    btnSendNumber.setText("재전송")
                    joinViewModel.clickSendButton()
                }

                layoutVerify.visibility = View.VISIBLE
            }
            btnVerify.setOnClickListener {
                val verifyNum = etJoinVerifyNumber.text.toString()
                verifyPhoneNumber(verifyNum)
            }
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_join1Fragment_to_join2Fragment)
            }
        }
    }

    private fun observeEditText(){
        binding.apply {
            etJoinPhoneNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(count < 11){
                        joinViewModel.initSendButton()
                        btnSendNumber.setText(resources.getString(R.string.send_verify_number))
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
        }
    }

    private val verifyCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            showToast("인증 코드가 전송되었습니다. 120초 이내에 입력해주세요.")
            verifyPhoneNumberWithCode(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d("test123", "onVerificationFailed: ${e.cause}")
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }

            showToast("인증실패")
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            storedVerificationId = verificationId //verificationId 와 전화번호인증코드 매칭해서 인증하는데 사용예정
            resendToken = token
        }
    }

    private fun sendVerifyMessagePhoneNumber(phoneNumber: String){

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(verifyCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    // 번호 인증하기
    private fun verifyPhoneNumber(verifyingMessage: String){
        val phoneCredential = PhoneAuthProvider.getCredential(storedVerificationId, verifyingMessage)
        verifyPhoneNumberWithCode(phoneCredential)
    }

    // 전화번호 인증 실행 (onCodeSent() 에서 받은 vertificationID 와
    // 문자로 받은 인증코드로 생성한 PhoneAuthCredential 사용)
    private fun verifyPhoneNumberWithCode(phoneAuthCredential: PhoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener(requireActivity()) { task ->
                Log.d("test123", "verifyPhoneNumberWithCode: ${task.exception}")
                if (task.isSuccessful) {
                    showToast("인증 성공")
                    joinViewModel.completeVerify()
                    binding.apply {
                        etJoinPhoneNumber.isEnabled = false
                        etJoinVerifyNumber.isEnabled = false
                        btnSendNumber.isEnabled = false
                        btnVerify.isEnabled = false
                    }

                } else {

                    showToast("다시 입력해주세요.")
                }
            }.addOnFailureListener {
                Log.d("test123", "verifyPhoneNumberWithCode fail: ${it}")
            }
    }

    // 전화번호 인증코드 재요청
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(verifyCallbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
}