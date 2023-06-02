package com.side.runwithme.view.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.example.seobaseview.base.BaseActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.side.domain.model.User
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityLoginBinding
import com.side.runwithme.util.*
import com.side.runwithme.util.preferencesKeys.EMAIL
import com.side.runwithme.util.preferencesKeys.JWT
import com.side.runwithme.util.preferencesKeys.REFRESH_TOKEN
import com.side.runwithme.util.preferencesKeys.SEQ
import com.side.runwithme.util.preferencesKeys.WEIGHT
import com.side.runwithme.view.MainActivity
import com.side.runwithme.view.join.JoinActivity
import com.side.runwithme.view.loading.LoadingDialog
import com.side.runwithme.view.login.LoginViewModel.Event

import dagger.hilt.android.AndroidEntryPoint
import initKeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    private val loginViewModel by viewModels<LoginViewModel>()



    override fun init() {

        requestPermission()

        binding.apply {
            loginVM = loginViewModel
        }

        initClickListener()

        initViewModelCallBack()
    }

    private fun initClickListener() {
        binding.apply {
            btnLoginKakao.setOnClickListener {

            }

            tvJoin.setOnClickListener {
                //회원가입
                startActivity(Intent(this@LoginActivity, JoinActivity::class.java))
            }

            btnLogin.setOnClickListener {
                initKeyStore(Calendar.getInstance().timeInMillis.toString())
                loginViewModel.loginWithEmail()
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
//                val token = event.data.token
                val user = event.data

                lifecycleScope.launch(Dispatchers.IO) {
                    saveUser(user)
                }

                lifecycleScope.launch {
                    // 로딩 0.5초간 정지
                    // user하고 token 저장하기 위함
                    loading()

                    showToast("로그인 성공")

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

            }
            is Event.Fail -> {
                showToast(event.message)
            }
        }
    }

    private suspend fun saveUser(user: User){
        dataStore.saveEncryptStringValue(EMAIL, user.email)
        dataStore.saveEncryptStringValue(SEQ, user.seq.toString())
        dataStore.saveValue(WEIGHT, user.weight)
    }

    private fun loading(){
        val loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        // 로딩이 진행되지 않았을 경우
        lifecycleScope.launch {
            delay(500)
            if(loadingDialog.isShowing){
                loadingDialog.dismiss()
            }
        }
    }
}

