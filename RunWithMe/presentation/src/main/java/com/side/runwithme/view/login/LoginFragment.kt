package com.side.runwithme.view.login

import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.side.data.util.initKeyStore
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentLoginBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.MainActivity
import com.side.runwithme.view.find_password.FindPasswordActivity
import com.side.runwithme.view.join.JoinActivity
import com.side.runwithme.view.loading.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel by viewModels<LoginViewModel>()
    private var loadingDialog: LoadingDialog? = null

    override fun init() {

        binding.apply {
            loginVM = loginViewModel
        }

        initClickListener()

        initViewModelCallBack()
    }

    private fun initClickListener() {
        binding.apply {
            tvJoin.setOnClickListener {
                //회원가입
                startActivity(Intent(requireActivity(), JoinActivity::class.java))
            }

            btnLogin.setOnClickListener {
                initKeyStore(Calendar.getInstance().timeInMillis.toString())
                loading()
                loginViewModel.loginWithEmail()
            }

            tvFindPassword.setOnClickListener {
                startActivity(Intent(requireActivity(), FindPasswordActivity::class.java))
            }

            btnLoginKakao.setOnClickListener {
                kakaoLogin()
            }
        }
    }

    private fun kakaoLogin() {
        val kakaoApi = UserApiClient.instance

        // 카카오톡 실행 불가능인 경우
        if (!kakaoApi.isKakaoTalkLoginAvailable(requireContext())) {
            kakaoApi.loginWithKakaoAccount(requireContext(), callback = kakaoLoginWithAccountCallback)
            return
        }

        kakaoApi.loginWithKakaoTalk(requireContext()) { token, error ->
            if (error != null) {

                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    return@loginWithKakaoTalk
                }

                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                kakaoApi.loginWithKakaoAccount(requireContext(), callback = kakaoLoginWithAccountCallback)

            } else if (token != null) {
                loginViewModel.loginWithKakao(token.accessToken)
            }
        }

    }

    private val kakaoLoginWithAccountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            showToast("로그인 실패하셨습니다. 다시 시도해주세요.")
        } else if (token != null) {

            loginViewModel.loginWithKakao(token.accessToken)
        }
    }

    private fun initViewModelCallBack() {
        repeatOnStarted {
            loginViewModel.loginEventFlow.collectLatest { event ->
                handleEvent(event)
            }
        }

    }

    private fun handleEvent(event: LoginViewModel.Event) {
        when (event) {
            is LoginViewModel.Event.Success -> {

                lifecycleScope.launch {

                    showToast("로그인 성공")
                    loadingDialog?.dismiss()

                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }

            }

            is LoginViewModel.Event.Fail -> {
                showToast(event.message)
            }

            is LoginViewModel.Event.NotInitalized -> {
                // join으로 보내기
                val action = LoginFragmentDirections.actionLoginFragmentToSocialJoinFragment(event.seq)
                findNavController().navigate(action)
            }
        }
    }

    private fun loading() {
        loadingDialog = LoadingDialog(requireContext())
        loadingDialog!!.show()
        // 로딩이 진행되지 않았을 경우
        lifecycleScope.launch {
            delay(500)
            if (loadingDialog?.isShowing ?: false) {
                loadingDialog?.dismiss()
            }
        }
    }

}