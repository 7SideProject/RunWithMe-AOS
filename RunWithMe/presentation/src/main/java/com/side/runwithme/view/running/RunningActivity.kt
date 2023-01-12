package com.side.runwithme.view.running

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.ViewGroup
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityRunningBinding
import com.side.runwithme.service.RunningService
import com.side.runwithme.util.ACTION_START_OR_RESUME_SERVICE
import com.side.runwithme.util.ACTION_STOP_SERVICE
import com.side.runwithme.util.GOAL_TYPE_TIME
import com.side.runwithme.util.TrackingUtility
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapView
import javax.inject.Inject

class RunningActivity : BaseActivity<ActivityRunningBinding>(R.layout.activity_running) {

    // 라이브 데이터를 받아온 값들
    private var isTracking = false
    private var currentTimeInMillis = 0L

    private var job: Job = Job()

    private var type: String = GOAL_TYPE_TIME

    override fun init() {
        initMapView()

        initClickListener()
        initObserve()
    }

    private fun firstStart() {
        if(!RunningService.isFirstRun){
            CoroutineScope(Dispatchers.Main).launch {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                delay(1000L)

            }
        }
    }

    private fun initObserve(){
        RunningService.isTracking.observe(this){
//            updateTracking(it)
        }

        // 시간(타이머) 경과 관찰
        RunningService.timeRunInMillis.observe(this){
            currentTimeInMillis = it
            Log.d("dddd", "initObserve: currentTimeInMillis : ${it}")
            val formattedTime = TrackingUtility.getFormattedStopWatchTimeSummery(it)

            changeTimeText(formattedTime)
        }
    }

    private fun changeTimeText(time: String){
        binding.apply {
            tvTime.text = time
//            if(type == GOAL_TYPE_TIME){
//            }
        }
    }

    private fun initClickListener(){
        binding.apply {
            btnStart.setOnClickListener {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            }

            btnStop.setOnClickListener {
                stopRun()
            }
        }
    }

    // 달리기 종료
    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun initMapView(){
        val mapView = MapView(this@RunningActivity)
        val mapViewContainer = binding.mapView as ViewGroup
        mapViewContainer.addView(mapView)

        mapView.currentLocationTrackingMode

    }

    // 서비스에게 명령을 전달함
    private fun sendCommandToService(action : String) {
        Intent(this, RunningService::class.java).also {
            it.action = action
            this.startService(it)
        }
        Log.d("dddd", "sendCommandToService: ")
    }


}