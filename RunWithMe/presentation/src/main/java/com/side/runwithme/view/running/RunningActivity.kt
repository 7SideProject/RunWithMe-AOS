package com.side.runwithme.view.running

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityRunningBinding
import com.side.runwithme.service.PolyLine
import com.side.runwithme.service.RunningService
import com.side.runwithme.util.*
import kotlinx.coroutines.*
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView


class RunningActivity : BaseActivity<ActivityRunningBinding>(R.layout.activity_running) {

    private lateinit var mapView:MapView

    // 라이브 데이터를 받아온 값들
    private var pathPoints = mutableListOf<PolyLine>()

    private lateinit var polyline:MapPolyline


    override fun init() {
        requestPermission()

        initMapView()

        initClickListener()

        initObserve()

        firstStart()

        initDrawLine()
    }

    private fun initClickListener(){
        binding.apply {
            btnStart.setOnClickListener {
                btnStart.visibility = View.GONE
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
    private fun initMapView(){
        mapView = MapView(this@RunningActivity)
        val mapViewContainer = binding.mapView as ViewGroup
        mapViewContainer.addView(mapView)

        mapView.setZoomLevelFloat(0.1F, true)
    }

    private fun initDrawLine() {
        polyline = MapPolyline()
        polyline.lineColor = ContextCompat.getColor(this, R.color.mainColor) // Polyline 컬러 지정.
    }

    private fun initObserve(){

        RunningService.pathPoints.observe(this){
            pathPoints = it
            drawPolyline()
        }

        // 시간(타이머) 경과 관찰
        RunningService.timeRunInMillis.observe(this){
            val formattedTime = TrackingUtility.getFormattedStopWatchTimeSummery(it)
            changeTimeText(formattedTime)
        }

        // 거리
        RunningService.sumDistance.observe(this){
            changeDistanceText(it)
        }

        val initSumDistance = 0F
        changeDistanceText(initSumDistance)
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

    private fun firstStart() {
        if(RunningService.isFirstRun){
            startTackingMode()

            CoroutineScope(Dispatchers.Main).launch {
                sendCommandToService(ACTION_SHOW_RUNNING_ACTIVITY)
                delay(3000L)
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                delay(1000L)

            }
        }
    }

    // 달리기 종료
    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    // 지도 위치 이동
    private fun startTackingMode(){
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading
    }


    private fun drawPolyline() {
        if(pathPoints.isEmpty()){
            return
        }
        if( pathPoints.last().isEmpty()){
            return
        }

        val lastPoint = pathPoints.last().last()

        polyline.addPoint(MapPoint.mapPointWithGeoCoord(lastPoint.latitude, lastPoint.longitude))
        // Polyline 지도에 올리기.
        mapView.addPolyline(polyline)

    }

    private fun changeTimeText(time: String){
        binding.apply {
            tvTime.text = time
        }
    }

    private fun changeDistanceText(sumDistance : Float){
        binding.apply {
            tvDistance.text = TrackingUtility.getFormattedDistance(sumDistance)
        }
    }


    // 서비스에게 명령을 전달함
    private fun sendCommandToService(action : String) {
        Intent(this, RunningService::class.java).also {
            it.action = action
            this.startService(it)
        }
    }


}