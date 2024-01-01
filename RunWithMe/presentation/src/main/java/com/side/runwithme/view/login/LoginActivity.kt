package com.side.runwithme.view.login

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityLoginBinding
import com.side.runwithme.util.*
import com.side.runwithme.view.MainActivity
import com.side.runwithme.view.find_password.FindPasswordActivity
import com.side.runwithme.view.join.JoinActivity
import com.side.runwithme.view.loading.LoadingDialog
import com.side.runwithme.view.login.LoginViewModel.Event
import com.side.runwithme.view.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint
import initKeyStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {


    private val loginViewModel by viewModels<LoginViewModel>()
    private var loadingDialog: LoadingDialog? = null

    override fun init() {

        checkFirstPermissionAlert()

        checkAutoLogin()

//        requestPermission()

        BearerError()

        binding.apply {
            loginVM = loginViewModel
        }

        initClickListener()

        initViewModelCallBack()
    }

    private fun checkFirstPermissionAlert() {
        loginViewModel.getPermissionCheck()
    }

    private fun checkAutoLogin() {
        loginViewModel.checkAutoLogin()
    }

    private fun initClickListener() {
        binding.apply {
            btnJoin.setOnClickListener {
                //회원가입
                startActivity(Intent(this@LoginActivity, JoinActivity::class.java))
            }

            btnLogin.setOnClickListener {
                initKeyStore(Calendar.getInstance().timeInMillis.toString())
                loading()
                loginViewModel.loginWithEmail()
            }

            tvFindPassword.setOnClickListener {
                startActivity(Intent(this@LoginActivity, FindPasswordActivity::class.java))
            }
        }
    }

    private fun initViewModelCallBack() {
        repeatOnStarted {
            loginViewModel.loginEventFlow.collectLatest { event ->
                handleEvent(event)

            }
        }

        repeatOnStarted {
            loginViewModel.permissionEventFlow.collectLatest {
                if (!it) {
                    getResult.launch(Intent(this@LoginActivity, PermissionActivity::class.java))
                } else {
                    requestPermission()
                }
            }
        }
    }

    val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == PERMISSON_RESULT_OK) {
            requestPermission()
            loginViewModel.savePermissionCheck()
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissionUpTo33()
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionUnder33()
        } else {
            checkPermissionUnder29()
        }
    }

    private fun checkPermissionUnder29() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {

                override fun onPermissionGranted() {
                    loginViewModel.savePermissionCheck()
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
                        showToast("위치 권한을 허가해주세요.")
                    }
                }
            })
            .setDeniedMessage("앱 사용을 위해 권한을 허용으로 설정해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionUnder33() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {

                override fun onPermissionGranted() {
                    if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_DENIED) {
                        permissionDialog()
                        return
                    }

                    loginViewModel.savePermissionCheck()
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
                        showToast("위치 권한을 허가해주세요.")
                    }
                }
            })
            .setDeniedMessage("앱 사용을 위해 권한을 허용으로 설정해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionUpTo33() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_DENIED) {

                        permissionDialog()
                        return
                    }

                    loginViewModel.savePermissionCheck()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Log.d("test123", "onPermissionDenied: ${deniedPermissions}")
                    if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        showToast("이미지 읽기 권한을 허가해주세요.")
                    }
                    else if (checkSelfPermission(Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        showToast("인터넷 권한을 허가해주세요.")
                    } else {
                        showToast("위치 권한을 허가해주세요.")
                    }
                }
            })
            .setDeniedMessage("앱 사용을 위해 권한을 허용으로 설정해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
            .check()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun permissionDialog(){
        var builder = AlertDialog.Builder(this)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")

        var listener = DialogInterface.OnClickListener { _, type ->
            when (type) {
                DialogInterface.BUTTON_POSITIVE -> backgroundPermission()
            }
        }
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", null)

        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun backgroundPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ), 2)
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.Success -> {

                lifecycleScope.launch {

                    showToast("로그인 성공")
                    loadingDialog?.dismiss()

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
        loadingDialog = LoadingDialog(this)
        loadingDialog!!.show()
        // 로딩이 진행되지 않았을 경우
        lifecycleScope.launch {
            delay(500)
            if (loadingDialog?.isShowing ?: false) {
                loadingDialog?.dismiss()
            }
        }
    }

    private fun BearerError() {
        val intent = intent
        val errorMsg = intent.getStringExtra("BearerError")
        if (errorMsg.isNullOrBlank()) return

        showToast(errorMsg)
    }
}

