package com.side.runwithme.view


import android.content.Intent
import android.view.View
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityTestOneBinding
import net.daum.mf.map.api.MapView
import java.util.concurrent.TimeUnit

class TestOneActivity : BaseActivity<ActivityTestOneBinding>(R.layout.activity_test_one) {
    override fun init() {
        val mapView = MapView(this)
        binding.mapView.addView(mapView)

        initClickListener()
        TestOneService.runTime.observe(this) {
            var milliseconds = it
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
            milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

            val timeText = "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"

            binding.tvTotalTime.text = timeText

        }

    }

    fun initClickListener() {
        binding.btnStart.setOnClickListener {
            val intent = Intent(this, TestOneService::class.java)
            startService(intent)
            binding.btnStart.visibility = View.INVISIBLE
            binding.btnPause.visibility = View.VISIBLE
        }

        binding.btnPause.setOnClickListener {
            TestOneService.isTracking.postValue(false)
            binding.btnStart.visibility = View.VISIBLE
            binding.btnPause.visibility = View.INVISIBLE
        }
    }


}