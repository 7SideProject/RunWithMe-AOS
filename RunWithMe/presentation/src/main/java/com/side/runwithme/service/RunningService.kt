package com.side.runwithme.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.side.runwithme.R
import com.side.runwithme.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import javax.inject.Inject

typealias PolyLine = MutableList<LatLng>
typealias Polylines = MutableList<PolyLine>

val TAG = "RunningService"
@AndroidEntryPoint
class RunningService : LifecycleService() {

    // 서비스 종료 여부
    private var serviceKilled = false

    // NotificationCompat.Builder 주입
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    // FusedLocationProviderClient 주입
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // NotificationCompat.Builder 수정하기 위함
//    lateinit var currentNotificationBuilder : NotificationCompat.Builder

    // 알림창에 표시될 시간
    private val timeRunInSeconds = MutableLiveData<Long>()

    private var isTimerEnabled = false // 타이머 실행 여부
    private var lapTime = 0L // 시작 후 측정한 시간
    private var totalTime = 0L // 정지 시 저장되는 시간
    private var timeStarted = 0L // 측정 시작된 시간
    private var lastSecondTimestamp = 0L // 1초 단위 체크를 위함

    // 러닝을 시작하거나 다시 시작한 시간
    private var startTime = 0L

    private var pauseLast = false

    private var pauseLatLng = LatLng(0.0,0.0)
    private var stopLastLatLng = LatLng(0.0, 0.0)

    companion object{
        val isTracking = MutableLiveData<Boolean>() // 위치 추적 상태 여부
        val pathPoints = MutableLiveData<Polylines>() // LatLng = 위도, 경도
        val timeRunInMillis = MutableLiveData<Long>() // 뷰에 표시될 시간
        var isFirstRun = true // 처음 실행 여부 (true = 실행되지않음)
        val sumDistance = MutableLiveData<Float>(0f)
        val startLatLng = MutableLiveData<LatLng>()  // 시작 지점
    }

    private fun initTextToSpeech() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Toast.makeText(this, "음성 안내를 지원하지 않는 버전입니다.", Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onCreate() {
        super.onCreate()
//        currentNotificationBuilder = baseNotificationBuilder
        postInitialValues()

        // 위치 추적 상태가 되면 업데이트 호출
        isTracking.observe(this){
            updateLocation(it)
            updateNotificationTrackingState(it)
        }
    }

    // 알림창 버튼 생성, 액션 추가
    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText = if (isTracking) "일시정지" else "다시 시작하기"
        // 정지 or 시작 버튼 클릭 시 그에 맞는 액션 전달
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, RunningService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            // pending Intent 객체 생성
            PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_MUTABLE)
        }else{
            val resumeIntent = Intent(this, RunningService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_MUTABLE)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 기존 action이 쌓이는 것을 방지
        baseNotificationBuilder.javaClass.getDeclaredField("mActions").apply{
            isAccessible = true
            set(baseNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

//        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply{
//            isAccessible = true
//            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
//        }

        // 서비스 종료상태가 아닐 때
        if(!serviceKilled){
//            currentNotificationBuilder = baseNotificationBuilder
            baseNotificationBuilder
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    notificationActionText,
                    pendingIntent
                )
            notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.build())
        }
    }


    // 초기화
    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            // 위치 추적 상태일 때
            while (isTracking.value!!){
                // 현재 시간 - 시작 시간 => 경과한 시간
                lapTime = System.currentTimeMillis() - timeStarted
                // 총시간 (일시정지 시 저장된 시간) + 경과시간 전달
                timeRunInMillis.postValue(totalTime + lapTime)
                // 알림창에 표시될 시간 초 단위로 계산함
                if(timeRunInMillis.value!! >= lastSecondTimestamp + 1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(50L)
            }

            // 위치 추적이 종료(정지) 되었을 때 총시간 저장
            totalTime += lapTime
        }
    }

    // 빈 polyline 추가
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf())) // null 이라면 초기화

    //위치 정보 요청
    @SuppressLint("MissingPermission")
    private fun updateLocation(isTracking: Boolean){
        if(isTracking and TrackingUtility.hasLocationPermissions(this)){
            val request = LocationRequest.create().apply {
                interval = LOCATION_UPDATE_INTERVAL
                fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                maxWaitTime = LOCATION_UPDATE_INTERVAL
            }
            fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        }
    }

    // 서비스가 종료되었을 때
    private fun killService(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        serviceKilled = true
        //isFirstRun = false
        pauseService()
        startTime = 0L
        pauseLast = false
        //count = 1

        postInitialValues()
//        stopForeground(true)
        stopSelf()
    }

    //위치정보 수신해 addPathPoint로 추가
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)

            if(isFirstRun){
                result?.locations?.let{ locations ->
                    for(location in locations){
                        startLatLng.postValue(LatLng(location.latitude, location.longitude))
                        pauseLatLng = LatLng(location.latitude, location.longitude)
                      
                    }
                }
            }
            if(isTracking.value!!){
                result?.locations?.let{ locations ->
                    for(location in locations){
                        addPathPoint(location)
                    }
                }
            }

            result?.locations?.let{ locations ->
                for(location in locations){
                    stopLastLatLng = LatLng(location.latitude, location.longitude)
                    resumeRunning()
                }
            }
        }
    }

    private fun resumeRunning(){
        val result = FloatArray(1)
        Location.distanceBetween(
            pauseLatLng.latitude,
            pauseLatLng.longitude,
            stopLastLatLng.latitude,
            stopLastLatLng.longitude,
            result
        )

        /** 이동이 없어 중지 상태일 때, 8m 이동하면 다시 시작 시킴**/



        val isResume = !isTracking.value!! and pauseLast
        if(isResume){ // 재시작
            Toast.makeText(this, "이동이 감지되어 러닝을 다시 시작합니다.", Toast.LENGTH_SHORT).show()
            startTimer()
            startTime = System.currentTimeMillis()
            pauseLast = false
        }

    }

    //위치 정보 추가
    private fun addPathPoint(location: Location?){
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
                distancePolyline()
            }
        }
    }

    // 거리 표시 (마지막 전, 마지막 경로 차이 비교)
    private fun distancePolyline(){
        val polylines = pathPoints.value!!
        val possiblePolyline = polylines.isNotEmpty() and (polylines.last().size > 1)

        if(possiblePolyline){
            val preLastLatLng = polylines.last().get(polylines.last().size - 2) // 마지막 전 경로
            val lastLatLng = polylines.last().last() // 마지막 경로

            // 이동거리 계산
            val result = FloatArray(1)
            Location.distanceBetween(
                preLastLatLng.latitude,
                preLastLatLng.longitude,
                lastLatLng.latitude,
                lastLatLng.longitude,
                result
            )

            // 비동기
            sumDistance.postValue(sumDistance.value!!.plus(result[0]))

            /** 5초 이상 이동했는데 이동거리가 2.3m 이하인 경우 정지하고, 마지막 위치를 기록함 (최소 오차 4초) **/

            /** 5초 이상 이동했는데 이동거리가 50m 이상인 경우 정지 (최소 오차 4초) -> 너무 빠른 경우 **/


        }

    }

    // 서비스가 호출 되었을 때
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let{
            when(it.action){
                // 시작, 재개 되었을 때
                ACTION_START_OR_RESUME_SERVICE ->{
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false

                    }else{
                        startTimer()
                    }
                    startTime = System.currentTimeMillis()
                }
                // 일시정지 되었을 때
                ACTION_PAUSE_SERVICE ->{
                    pauseService()
                }
                // 종료 되었을 때
                ACTION_STOP_SERVICE ->{
                    killService()
                }
                // 처음 화면 켰을 때
                ACTION_SHOW_RUNNING_ACTIVITY ->{
                    updateLocation(true)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Notification 등록, 서비스 시작
    private fun startForegroundService(){
        startTimer()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        // 초가 흐를 때마다 알림창의 시간 갱신
        timeRunInSeconds.observe(this){
            // 서비스 종료 상태가 아닐 때
            val notification = baseNotificationBuilder.setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }
    }

    // 채널 만들기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW // 알림음 없음
        )
        notificationManager.createNotificationChannel(channel)
    }

    // 서비스 정지
    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

}