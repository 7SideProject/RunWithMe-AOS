package com.side.runwithme.view


import android.content.Intent
import android.view.View
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityTestOneBinding
import com.side.runwithme.util.*
import net.daum.mf.map.api.MapView
import java.util.concurrent.TimeUnit

class TestOneActivity : BaseActivity<ActivityTestOneBinding>(R.layout.activity_test_one) {
    private var isPause = false
    override fun init() {
        val mapView = MapView(this)
        binding.mapView.addView(mapView)

        initClickListener()
        TestOneService.runTime.observe(this) {
            binding.tvTotalTime.text = runningTimeFormatter(it)
        }

    }

    fun initClickListener() {
        binding.apply {
            btnStart.setOnClickListener {
                sendServiceAction(ACTION_START_SERVICE)
                btnStop.visibility = View.VISIBLE
            }
            btnPause.setOnClickListener {
                if(isPause){
                    sendServiceAction(ACTION_RESUME_SERVICE)
                }
                else {
                    sendServiceAction(ACTION_PAUSE_SERVICE)
                }
                isPause = !isPause
            }
            btnStop.setOnClickListener {
                sendServiceAction(ACTION_STOP_SERVICE)
            }
        }
    }

    private fun sendServiceAction(action: String){
        val intent = Intent(this@TestOneActivity, TestOneService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }

}