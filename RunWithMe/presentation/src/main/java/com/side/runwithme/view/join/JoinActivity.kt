package com.side.runwithme.view.join

import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.seobaseview.base.BaseActivity
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityJoinBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinActivity : BaseActivity<ActivityJoinBinding>(R.layout.activity_join) {

    private lateinit var navController : NavController

    override fun init() {

        initNavigation()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

    }

    // backpressed에서 스택 pop해주던거 자동으로 됨
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // 홈 화면에서 뒤로가기 2번 클릭 시 종료
    var waitTime = 0L
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(navController.currentDestination?.id == R.id.join1Fragment) {
                if (System.currentTimeMillis() - waitTime >= 1500) {
                    waitTime = System.currentTimeMillis()
                    showToast("뒤로가기 버튼을 한번 더 누르면 회원가입이 종료됩니다.")
                } else {
                    finish()
                }
            }else{
                navController.popBackStack()
            }
        }
    }

}