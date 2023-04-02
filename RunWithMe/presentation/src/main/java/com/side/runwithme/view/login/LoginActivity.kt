package com.side.runwithme.view.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.example.seobaseview.base.BaseActivity
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityLoginBinding
import com.side.runwithme.util.KAKAO_LOGIN_URL
import com.side.runwithme.util.KAKAO_REST_API_KEY
import com.side.runwithme.util.REDIRECT_URL
import com.side.runwithme.view.join.JoinActivity
import java.util.*

class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val loginViewModel by viewModels<LoginViewModel>()

    private val uniqueState = UUID.randomUUID().toString()


    override fun init() {

        initClickListener()

        initViewModelCallBack()
    }

    private fun initClickListener(){
        binding.apply {
            btnLoginKakao.setOnClickListener {
                loadWebview()
            }
        }
    }

    private fun initViewModelCallBack(){
        loginViewModel.joinEvent.observe(this){
            startActivity(Intent(this, JoinActivity::class.java))
        }
    }

    private val webViewClientResponseLogin: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            request?.let {
                if (request.url.toString().startsWith(REDIRECT_URL)) {
                    val responseState = request.url.getQueryParameter("state")
                    if (responseState == uniqueState) {
                        request.url.getQueryParameter("code")?.let { code ->
                            // code를 서버로 전송
                            loginViewModel.login(code, uniqueState)
                        } ?: run {
                            // 로그인 에러
                            showToast("다시 로그인해주세요.")
                        }
                    }
                }

            }
            return super.shouldOverrideUrlLoading(view, request)

        }
    }
    fun loadWebview(){

        val uri = Uri.parse(KAKAO_LOGIN_URL)
            .buildUpon()
            .appendQueryParameter("client_id", KAKAO_REST_API_KEY)
            .appendQueryParameter("redirect_uri", REDIRECT_URL)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("state", uniqueState)
            .build()

        binding.webviewKakao.apply {
            visibility = View.VISIBLE
            loadUrl(uri.toString())
            webViewClient = webViewClientResponseLogin
        }
    }


}

