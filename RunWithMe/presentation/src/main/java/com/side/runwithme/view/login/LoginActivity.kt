package com.side.runwithme.view.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.example.seobaseview.base.BaseActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityLoginBinding
import com.side.runwithme.util.*
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissionUpTo33()
        } else {
            checkPermissionUnder33()
        }
    }

    private fun checkPermissionUnder33() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {

                override fun onPermissionGranted() {

                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        showToast("파일 읽기 권한을 허가해주세요.")
                    } else if (checkSelfPermission(Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        showToast("인터넷 권한을 허가해주세요.")
                    } else {
                        showToast("파일 읽기, 인터넷 권한을 허가해주세요.")
                    }
                }
            })
            .setDeniedMessage("앱 사용을 위해 권한을 허용으로 설정해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionUpTo33() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onPermissionGranted() {
                    if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_DENIED
                    ) {

                    }
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        showToast("이미지 읽기 권한을 허가해주세요.")
                    }
                    if (checkSelfPermission(Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        showToast("인터넷 권한을 허가해주세요.")
                    }
                    showToast("파일 읽기, 인터넷 권한을 허가해주세요.")
                }
            })
            .setDeniedMessage("앱 사용을 위해 권한을 허용으로 설정해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_MEDIA_IMAGES
            )
            .check()
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.Success -> {

                lifecycleScope.launch {
                    // 로딩 0.4초간 정지
                    // dataStore에 저장하는 과정 기다리기
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



    private fun loading() {
        val loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        // 로딩이 진행되지 않았을 경우
        lifecycleScope.launch {
            delay(500)
            if (loadingDialog.isShowing) {
                loadingDialog.dismiss()
            }
        }
    }
}

