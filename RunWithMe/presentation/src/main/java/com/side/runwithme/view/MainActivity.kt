package com.side.runwithme.view

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.seobaseview.base.BaseActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityMainBinding
import com.side.runwithme.service.RunningService
import com.side.runwithme.service.SERVICE_ISRUNNING
import com.side.runwithme.view.running.RunningActivity
import com.side.runwithme.view.running_list.RunningListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main){

    private lateinit var navController : NavController
    
    override fun init() {
        requestPermission()

        runningCheck()

        initNavigation()

        initClickListener()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun runningCheck(){
        // 트래킹이 종료되지 않았을 때, 백그라운드에서 제거 후 실행해도 바로 트래킹 화면이 뜨게함
        if(RunningService.serviceState == SERVICE_ISRUNNING){
            startActivity(Intent(this, RunningActivity::class.java))
        }
    }

    private fun initClickListener(){
        binding.apply {
            floatingActionButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, RunningListActivity::class.java))
            }
            btn2.setOnClickListener {
                startActivity(Intent(this@MainActivity, RunningActivity::class.java))
            }
        }
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

    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionUpTo33()
        }else {
            requestPermissionUnder33()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissionUpTo33(){
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {

                override fun onPermissionGranted() {
                    if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_DENIED) {
                        permissionDialog()
                    }
                }
                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    showToast("권한을 허가해주세요.")
                }
            })
            .setDeniedMessage("앱 사용을 위해 권한을 허용으로 설정해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
            .check()
    }
    private fun requestPermissionUnder33(){
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onPermissionGranted() {
                    if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_DENIED) {
                        permissionDialog()
                    }
                }
                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    showToast("권한을 허가해주세요.")
                }
            })
            .setDeniedMessage("앱 사용을 위해 권한을 허용으로 설정해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }


    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        binding.expandableBottomBar.apply {
            setupWithNavController(navController)
            background = null
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // 바텀 네비게이션이 표시되는 Fragment
            if(destination.id == R.id.HomeFragment || destination.id == R.id.MyPageFragment){
                if(binding.expandableBottomBar.visibility == View.GONE) {
                    binding.apply {
                        expandableBottomBar.visibility = View.VISIBLE
                        bottomAppBar.visibility = View.VISIBLE
                        floatingActionButton.visibility = View.VISIBLE
                        view.visibility = View.VISIBLE
                    }
                }
            }
            // 바텀 네비게이션이 표시되지 않는 Fragment
            else{
                if(binding.expandableBottomBar.visibility == View.VISIBLE) {
                    binding.apply {
                        expandableBottomBar.visibility = View.GONE
                        bottomAppBar.visibility = View.GONE
                        floatingActionButton.visibility = View.GONE
                        view.visibility = View.GONE
                    }
                }
            }
        }
    }

    // 홈 화면에서 뒤로가기 2번 클릭 시 종료
    var waitTime = 0L
//    override fun onBackPressed() {
//        if(navController.currentDestination?.id == R.id.HomeFragment) {
//            if (System.currentTimeMillis() - waitTime >= 1500) {
//                waitTime = System.currentTimeMillis()
//                showToast("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
//            } else {
//                finish()
//            }
//        }
//        else{
//            super.onBackPressed()
//        }
//    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(navController.currentDestination?.id == R.id.HomeFragment) {
                if (System.currentTimeMillis() - waitTime >= 1500) {
                    waitTime = System.currentTimeMillis()
                    showToast("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
                } else {
                    finish()
                }
            }else {
                navController.popBackStack()
            }
        }
    }
}