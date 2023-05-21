package com.side.runwithme.view.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.seobaseview.base.BaseActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.side.domain.model.User
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityLoginBinding
import com.side.runwithme.util.KAKAO_LOGIN_URL
import com.side.runwithme.util.KAKAO_REST_API_KEY
import com.side.runwithme.util.REDIRECT_URL
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.join.JoinActivity
import com.side.runwithme.view.login.LoginViewModel.Event

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val loginViewModel by viewModels<LoginViewModel>()

    private val uniqueState = UUID.randomUUID().toString()


    override fun init() {

        requestPermission()

        initClickListener()

        initViewModelCallBack()
    }

    private fun initClickListener() {
        binding.apply {
            btnLoginKakao.setOnClickListener {
                loadWebview()
            }

            btnJoin.setOnClickListener {
                //회원가입
                startActivity(Intent(this@LoginActivity, JoinActivity::class.java))
            }

            btnLogin.setOnClickListener {
                loginViewModel.loginWithEmail(
                    User(
                        seq = 0L,
                        email = "abcdef@naver.com",
                        password = "12341234",
                        nickname = "닉네임1",
                        height = 100,
                        weight = 100,
                        point = 0,
                        profileImgSeq = 0L
                    )
                )
            }
        }
    }

    private fun initViewModelCallBack() {
        repeatOnStarted {
            loginViewModel.loginEventFlow.collectLatest { event ->
                handleEvent(event)

            }
        }
    }

//    private val webViewClientResponseLogin: WebViewClient = object : WebViewClient() {
//        override fun shouldOverrideUrlLoading(
//            view: WebView?,
//            request: WebResourceRequest?
//        ): Boolean {
//            request?.run {
//                if (url.toString().startsWith(REDIRECT_URL)) {
//                    val responseState = url.getQueryParameter("state")
//                    if (responseState == uniqueState) {
//                        val code = url.getQueryParameter("code")
//                        Log.d("eettt", "shouldOverrideUrlLoading: $code")
//
//                        url.getQueryParameter("code")?.let { code ->
//                            // code를 서버로 전송
////                            loginViewModel.login(code, uniqueState)
//                        } ?: run {
//                            // 로그인 에러
//                            showToast("다시 로그인해주세요.")
//                        }
//                    }
//                }
//
//            }
//
//            return super.shouldOverrideUrlLoading(view, request)
//        }
//    }

    fun loadWebview() {

        val uri = Uri.parse(KAKAO_LOGIN_URL)
            .buildUpon()
            .appendQueryParameter("client_id", KAKAO_REST_API_KEY)
            .appendQueryParameter("redirect_uri", REDIRECT_URL)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("state", uniqueState)
            .build()


//        binding.webviewKakao.apply {
//            visibility = View.VISIBLE
//            webViewClient = webViewClientResponseLogin
//            settings.loadWithOverviewMode = true // WebView 화면 크기에 맞추도록 설정
//            settings.useWideViewPort = true // wide viewport 설정 - loadWithOverviewMode와 같이 써야함
//            settings.javaScriptEnabled = true
//            loadUrl(uri.toString())
//        }
    }


    private fun requestPermission() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onPermissionGranted() {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                    ) {

                    }
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    showToast("권한을 허가해주세요.")
                }
            })
            .setDeniedMessage("앱 사용을 위해 권한을 허용으로 설정해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.Success -> {
                showToast(event.message)

            }
            is Event.Fail -> {
                showToast(event.message)
            }
        }
    }
}

