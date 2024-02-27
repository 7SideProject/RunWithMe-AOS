package com.side.runwithme.view.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityLoginBinding
import com.side.runwithme.util.PERMISSON_RESULT_OK
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.loading.LoadingDialog
import com.side.runwithme.view.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {


    private val loginViewModel by viewModels<LoginViewModel>()
    private lateinit var navController : NavController

    override fun init() {

        initNavigation()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        checkFirstPermissionAlert()

        checkAutoLogin()

        BearerError()

        initViewModelCallBack()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

    }

    private fun checkFirstPermissionAlert() {
        loginViewModel.getPermissionCheck()
    }

    private fun checkAutoLogin() {
        loginViewModel.checkAutoLogin()
    }

    private fun initViewModelCallBack() {

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

    val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == PERMISSON_RESULT_OK) {
            requestPermission()
            loginViewModel.savePermissionCheck()
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissionUpTo33()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

                    loginViewModel.savePermissionCheck()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        showToast("이미지 읽기 권한을 허가해주세요.")
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
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
            .check()
    }

    private fun BearerError() {
        val intent = intent
        val errorMsg = intent.getStringExtra("BearerError")
        if (errorMsg.isNullOrBlank()) return

        showToast(errorMsg)
    }

    // 홈 화면에서 뒤로가기 2번 클릭 시 종료
    var waitTime = 0L
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(navController.currentDestination?.id == R.id.loginFragment) {
                if (System.currentTimeMillis() - waitTime >= 1500) {
                    waitTime = System.currentTimeMillis()
                    showToast("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
                } else {
                    finish()
                }
            }else{
                navController.popBackStack()
            }
        }
    }
}

