package com.side.runwithme.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.naver.maps.geometry.LatLng
import com.side.runwithme.R
import com.side.runwithme.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.time.LocalDate
import javax.inject.Inject

typealias PolyLine = MutableList<LatLng>
const val SERVICE_NOTSTART = 0
const val SERVICE_ISRUNNING = 1

@AndroidEntryPoint
class RunningService : LifecycleService() {

    // Binder
    private val binder = LocalBinder()

    companion object {
        // 서비스 종료 여부
        var serviceState = SERVICE_NOTSTART // 앱 강제 종료 후 재시작시, SERVICE_ISRUNNING 상태이면 재시작
    }

    // NotificationCompat.Builder 주입
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    // FusedLocationProviderClient 주입
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // NotificationCompat.Builder 수정하기 위함, 없으면 notification 삭제 안됨
    lateinit var currentNotificationBuilder: NotificationCompat.Builder

    // 알림창에 표시될 시간
    private val timeRunInSeconds = MutableLiveData<Long>()

    private var lapTime = 0L // 시작 후 측정한 시간
    private var totalTime = 0L // 정지 시 저장되는 시간
    private var timeStarted = 0L // 측정 시작된 시간
    private var lastSecondTimestamp = 0L // 1초 단위 체크를 위함

    // 러닝을 시작하거나 다시 시작한 시간
    var startTime = 0L

    var isFirstRun = true // 처음 실행 여부 (true = 실행되지않음)
    var startDay = ""

    private var pauseLast = false
    var stopRunningBeforeRegister = false // 정지 버튼을 누르고 서버에 기록 등록하기 전 상태 (오류 시 다시 등록하기 위함)

    private var pauseLatLng = LatLng(0.0, 0.0)
    private var stopLastLatLng = LatLng(0.0, 0.0)

    val isRunning = MutableLiveData<Boolean>() // 위치 추적 상태 여부
    val pathPoints = MutableLiveData<PolyLine>() // LatLng = 위도, 경도
    val timeRunInMillis = MutableLiveData<Long>() // 뷰에 표시될 시간
    val sumDistance = MutableLiveData<Float>(0f)

    inner class LocalBinder : Binder() {
        fun getService(): RunningService = this@RunningService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        serviceState = SERVICE_ISRUNNING
        postInitialValues()

        initObserve()
    }


    private fun initObserve() {
        // 위치 추적 상태가 되면 업데이트 호출
        isRunning.observe(this) {
            updateLocation(it)
            updateNotificationTrackingState(it)
        }
    }

    // 초기화
    private fun postInitialValues() {
        isRunning.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    private fun resumeRunning() {
        if(stopRunningBeforeRegister){
            return
        }

        val result = FloatArray(1)
        Location.distanceBetween(
            pauseLatLng.latitude,
            pauseLatLng.longitude,
            stopLastLatLng.latitude,
            stopLastLatLng.longitude,
            result
        )

        // 이동이 없어 중지 상태일 때, 8m 이동하면 다시 시작 시킴
        val isMoving = result[0] > 8f && ((System.currentTimeMillis() - startTime) > 4000L)
        val isResume = !isRunning.value!! and pauseLast

        if (isMoving && isResume) { // 재시작
            showToast("이동이 감지되어 러닝을 다시 시작합니다.")
            startTimer()
            startTime = System.currentTimeMillis()
            pauseLast = false
        }
    }


    private fun startTimer() {
        isRunning.postValue(true)
        timeStarted = System.currentTimeMillis()
        startTimerJob()

    }




    private fun startTimerJob() {
        lifecycleScope.launch(Dispatchers.Main) {
            // 러닝 중 일 때
            while (isRunning.value!!) {
                // 현재 시간 - 시작 시간 => 경과한 시간
                lapTime = System.currentTimeMillis() - timeStarted
                // 총시간 (일시정지 시 저장된 시간) + 경과시간 전달
                timeRunInMillis.postValue(totalTime + lapTime)
                // 알림창에 표시될 시간 초 단위로 계산함
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }

            // 위치 추적이 종료(정지) 되었을 때 총시간 저장
            totalTime += lapTime
        }
    }

    //위치 정보 요청
    @SuppressLint("MissingPermission", "VisibleForTests")
    private fun updateLocation(isTracking: Boolean) {
        if(stopRunningBeforeRegister){
            return
        }

        if (isTracking and TrackingUtility.hasLocationPermissions(this)) {
            val request = LocationRequest.create().apply {
                interval = LOCATION_UPDATE_INTERVAL
                fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                maxWaitTime = LOCATION_UPDATE_INTERVAL
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }


    //위치정보 수신해 addPathPoint로 추가
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            // 처음 시작할 때
            if (isFirstRun) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        pauseLatLng = LatLng(location.latitude, location.longitude)
                    }
                }
            }
            // 러닝 중
            if (isRunning.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                    }
                }
                return
            }

            // 일시 정지했을 때
            result?.locations?.let { locations ->
                for (location in locations) {
                    stopLastLatLng = LatLng(location.latitude, location.longitude)
                    resumeRunning()
                }
            }
        }
    }


    //위치 정보 추가
    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                add(pos)
                pathPoints.postValue(this)
                distancePolyline()

                // 경로 최적화
                if (pathPoints.value!!.size >= 4) {
                    optimizationPolyLine()
                }
            }
        }
    }

    // 경로 최적화 알고리즘
    // 좌표 4개 이상일 때 이전 3개의 좌표를 비교하여
    // 거리가 가깝거나 가는 방향에서 각도가 완만한 경우 좌표 삭제
    private fun optimizationPolyLine() {
        /** polyline이 변경되었을 때 pathPoints.value 변경되었을듯 싶은데 확인해보기 **/
        val polyLine = pathPoints.value
        val first = polyLine!!.get(polyLine.size - 4)
        val second = polyLine!!.get(polyLine.size - 3)
        val third = polyLine!!.get(polyLine.size - 2)


        val result = FloatArray(1)
        Location.distanceBetween(
            second.latitude,
            second.longitude,
            third.latitude,
            third.longitude,
            result
        )

        val lastDistance = result[0]

        // 10미터 미만으로 너무 가까운 점이면 삭제
        if (lastDistance < 10) {
            polyLine.removeAt(polyLine.size - 3)
            return
        }

        // 100미터 이상으로 너무 먼 점이면 각도 상관없이 무조건 저장
        if (lastDistance >= 100) return

        // 적당한 거리라면 각도 비교
        val v1: Vector = getVector(first, second) // 기존 벡터
        val v2: Vector = getVector(second, third) // 비교할 벡터

        val cosine = getVectorDotProduct(v1, v2) / (getVectorDistance(v1) * getVectorDistance(v2))

        // 거리가 가까울 땐 +- 5도 (거의 직선인 경우 삭제)
        if (lastDistance < 50) {
            if (cosine > Math.cos(deg2rad(20.0))) {
                polyLine.removeAt(polyLine.size - 3)
                return
            }
        } else { // 거리가 멀어지면 여유범위 줄이기
            if (cosine > Math.cos(deg2rad(10.0))) {
                polyLine.removeAt(polyLine.size - 3)
                return
            }

        }

    }

    // This function converts decimal degrees to radians
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private class Vector(var x: Double, var y: Double)

    // 두 좌표를 위도에 따른 경도의 거리를 적용해서 최대한 오차없는 평면벡터화 한 것
    private fun getVector(point1: LatLng, point2: LatLng): Vector {
        return Vector(
            (point2.latitude - point1.latitude) / Math.cos(deg2rad(point2.latitude)),
            point2.longitude - point1.longitude
        )
    }

    // 벡터의 크기
    private fun getVectorDistance(v: Vector): Double {
        return Math.sqrt(v.x * v.x + v.y * v.y)
    }

    // 벡터의 내적
    private fun getVectorDotProduct(v1: Vector, v2: Vector): Double {
        return v1.x * v2.x + v1.y * v2.y
    }

    // 거리 표시 (마지막 전, 마지막 경로 차이 비교)
    private fun distancePolyline() {
        val polylines = pathPoints.value!!
        val possiblePolyline = polylines.isNotEmpty() && polylines.size >= 2

        if (possiblePolyline) {
            val preLastLatLng = polylines.get(polylines.size - 2) // 마지막 전 경로
            val lastLatLng = polylines.last() // 마지막 경로

            // 이동거리 계산
            val result = FloatArray(1)
            Location.distanceBetween(
                preLastLatLng.latitude,
                preLastLatLng.longitude,
                lastLatLng.latitude,
                lastLatLng.longitude,
                result
            )

            sumDistance.postValue(sumDistance.value!!.plus(result[0]))

            // 5초 이상 이동했는데 이동거리가 2.5m 이하인 경우 정지하고, 마지막 위치를 기록함 (최소 오차 4초)
            val isNotMoving = result[0] < 2.5f && (System.currentTimeMillis() - startTime) > 4000L
            if (isNotMoving) {
                showToast("이동이 없어 러닝이 일시 중지되었습니다.")
                pauseLatLng = lastLatLng
                pauseLast = true
                pauseService()
            }

            // 5초 이상 이동했는데 이동거리가 60m 이상인 경우 정지 (최소 오차 4초) -> 너무 빠른 경우
            val isFastMoving = result[0] > 60f && (System.currentTimeMillis() - startTime) > 4000L
            if (isFastMoving) {
                showToast("비정상적인 이동이 감지되어 러닝이 일시 중지되었습니다.")
                pauseService()
            }
        }
    }

    // 서비스가 호출 되었을 때
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // service 재시작시
        if(intent == null){
            return START_STICKY
        }

        intent.let {
            when (it.action) {
                // 시작 되었을 때
                ACTION_START_SERVICE -> {
                    startTimer()
                    startForegroundService()
                    isFirstRun = false
                    startTime = System.currentTimeMillis()
                    startDay = LocalDate.now().toString()
                }
                // 재개 되었을 때
                ACTION_RESUME_SERVICE -> {
                    startTimer()
                    startTime = System.currentTimeMillis()
                }
                // 일시정지 되었을 때
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                // 종료 되었을 때
                ACTION_STOP_SERVICE -> {
                    killService()
                }
                // 처음 화면 켰을 때
                ACTION_SHOW_RUNNING_ACTIVITY -> {
                    updateLocation(true)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    // 서비스 정지
    private fun pauseService() {
        isRunning.postValue(false)
    }

    // 서비스가 종료되었을 때
    private fun killService() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        pauseService()
        serviceState = SERVICE_NOTSTART
        startTime = 0L
        pauseLast = false
        isFirstRun = true

        postInitialValues()
        /** stopForeground 빼고 해보기 **/
//        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // Notification 등록, 서비스 시작
    private fun startForegroundService() {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        // 초가 흐를 때마다 알림창의 시간 갱신
        timeRunInSeconds.observe(this) {
            // 서비스 종료 상태가 아닐 때
            if (serviceState == SERVICE_ISRUNNING) {
                val notification = currentNotificationBuilder.setContentText(
                    TrackingUtility.getFormattedStopWatchTime(it * 1000L)
                )
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }
    }

    // 채널 만들기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW // 알림음 없음
        )
        notificationManager.createNotificationChannel(channel)
    }

    // 알림창 버튼 생성, 액션 추가
    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "일시정지" else "다시 시작하기"
        // 정지 or 시작 버튼 클릭 시 그에 맞는 액션 전달
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, RunningService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            // pending Intent 객체 생성
            PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            val resumeIntent = Intent(this, RunningService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_MUTABLE)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 기존 action이 쌓이는 것을 방지
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        // 서비스 종료상태가 아닐 때
        if (serviceState == SERVICE_ISRUNNING) {
            currentNotificationBuilder = baseNotificationBuilder
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    notificationActionText,
                    pendingIntent
                )
            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}