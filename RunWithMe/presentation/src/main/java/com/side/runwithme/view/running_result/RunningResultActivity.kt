package com.side.runwithme.view.running_result

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.seobaseview.base.BaseActivity
import com.side.domain.model.AllRunRecord
import com.side.domain.model.RunRecord
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningListBinding
import okhttp3.MultipartBody

class RunningResultActivity : BaseActivity<ActivityRunningListBinding>(R.layout.activity_running_result) {

    private lateinit var navController : NavController

    private val runningResultViewModel : RunningResultViewModel by viewModels<RunningResultViewModel>()

    override fun init() {
        initNavigation()

        initIntentExtra()

    }

    private fun initIntentExtra(){
        val intent = Intent()
        var allRunRecord : AllRunRecord? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("allRunRecord", AllRunRecord::class.java)
        }else {
            intent.getSerializableExtra("allRunRecord") as AllRunRecord
        }

        runningResultViewModel.apply {
            setRunRecord(allRunRecord?.runRecord)
            setImgFile(allRunRecord?.imgFile)
        }

    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_result) as NavHostFragment
        navController = navHostFragment.navController
    }

}