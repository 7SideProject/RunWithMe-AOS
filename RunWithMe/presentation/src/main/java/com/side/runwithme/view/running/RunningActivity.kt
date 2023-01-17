package com.side.runwithme.view.running

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityRunningBinding
import com.side.runwithme.service.RunningService
import com.side.runwithme.util.*
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapView

class RunningActivity : BaseActivity<ActivityRunningBinding>(R.layout.activity_running) {

    private lateinit var mapView:MapView

    // 라이브 데이터를 받아온 값들
    private var isTracking = false
    private var currentTimeInMillis = 0L

    private var job: Job = Job()

    private var type: String = GOAL_TYPE_TIME

    override fun init() {
        requestPermission{
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
               permissionDialog()
            }
        }
        initMapView()
        initClickListener()
        initObserve()

        firstStart()
    }

    private fun permissionDialog(){
        var builder = AlertDialog.Builder(this)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when (p1) {
                DialogInterface.BUTTON_POSITIVE -> backgroundPermission()
            }
        }
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", null)

        builder.show()
    }

    private fun backgroundPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ), 2)
    }

    private fun requestPermission(logic : () -> Unit){
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
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

    private fun firstStart() {
        Log.d(TAG, "firstStart ${RunningService.isFirstRun}")
        if(RunningService.isFirstRun){
            RunningService.startLatLng.observe(this){
                Log.d(TAG, "firstStart: ${it}")
                moveCamera()
            }
            CoroutineScope(Dispatchers.Main).launch {
                sendCommandToService(ACTION_SHOW_RUNNING_ACTIVITY)
                delay(3000L)
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                delay(1000L)

            }
        }
    }

    // 지도 위치 이동
    private fun moveCamera(){
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading

        Log.d(TAG, "moveCamera: map ${mapView.mapCenterPoint.mapPointGeoCoord.latitude} ${mapView.mapCenterPoint.mapPointGeoCoord.longitude}")
    }

    private fun initObserve(){
        RunningService.isTracking.observe(this){
//            updateTracking(it)
        }

        RunningService.pathPoints.observe(this){
//            pathPoints = it
//            Log.d(TAG, "initObserve: it : ${it}")
//            Log.d(TAG, "moveCamera: mapPointCONGCoord ${mapView.mapCenterPoint.mapPointCONGCoord.x} ${mapView.mapCenterPoint.mapPointCONGCoord.y}")
        }

        // 시간(타이머) 경과 관찰
        RunningService.timeRunInMillis.observe(this){
            currentTimeInMillis = it
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
                it.visibility = View.GONE
                btnPause.visibility = View.VISIBLE

                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            }

            btnPause.setOnClickListener{
                btnStart.visibility = View.VISIBLE
                btnPause.visibility = View.GONE

                sendCommandToService(ACTION_PAUSE_SERVICE)
            }

            btnStop.setOnClickListener {
                btnStart.visibility = View.VISIBLE
                btnPause.visibility = View.GONE

                stopRun()
            }
        }
    }

    // 달리기 종료
    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun initMapView(){
        mapView = MapView(this@RunningActivity)
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
    }


}