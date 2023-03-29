package com.side.runwithme.view.running_result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.seobaseview.base.BaseActivity
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningListBinding

class RunningResultActivity : BaseActivity<ActivityRunningListBinding>(R.layout.activity_running_result) {

    private lateinit var navController : NavController

    override fun init() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_result) as NavHostFragment
        navController = navHostFragment.navController
    }

}