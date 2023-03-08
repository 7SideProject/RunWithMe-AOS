package com.side.runwithme.view

import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.seobaseview.base.BaseActivity
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main){

    private lateinit var navController : NavController
    
    override fun init() {

    }


//    private fun initNavigation() {
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
//        navController = navHostFragment.navController
//        binding.expandableBottomBar.apply {
//            setupWithNavController(navController)
//            background = null
//        }
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            // 바텀 네비게이션이 표시되는 Fragment
//            if(destination.id == R.id.HomeFragment || destination.id == R.id.MyPageFragment){
//                if(binding.expandableBottomBar.visibility == View.GONE) {
//                    binding.apply {
//                        expandableBottomBar.visibility = View.VISIBLE
//                        bottomAppBar.visibility = View.VISIBLE
//                        floatingActionButton.visibility = View.VISIBLE
//                        view.visibility = View.VISIBLE
//                    }
//                }
//            }
//            // 바텀 네비게이션이 표시되지 않는 Fragment
//            else{
//                if(binding.expandableBottomBar.visibility == View.VISIBLE) {
//                    binding.apply {
//                        expandableBottomBar.visibility = View.GONE
//                        bottomAppBar.visibility = View.GONE
//                        floatingActionButton.visibility = View.GONE
//                        view.visibility = View.GONE
//                    }
//                }
//            }
//        }
//    }
//
//    // 홈 화면에서 뒤로가기 2번 클릭 시 종료
//    var waitTime = 0L
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
}