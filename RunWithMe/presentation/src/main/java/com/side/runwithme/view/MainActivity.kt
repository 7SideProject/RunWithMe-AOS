package com.side.runwithme.view

import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.seobaseview.base.BaseActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.navigation.ui.setupWithNavController
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityMainBinding
import com.side.runwithme.view.running_list.RunningListActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main){

    private lateinit var navController : NavController
    
    override fun init() {
        requestPermission()

        initNavigation()

        initClickListener()
    }

    private fun initClickListener(){
        binding.apply {
            floatingActionButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, RunningListActivity::class.java))
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
    override fun onBackPressed() {
        if(navController.currentDestination?.id == R.id.HomeFragment) {
            if (System.currentTimeMillis() - waitTime >= 1500) {
                waitTime = System.currentTimeMillis()
                showToast("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
            } else {
                finish()
            }
        }
        else{
            super.onBackPressed()
        }
    }
}