package com.side.runwithme.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.side.domain.usecase.datastore.GetTTSOptionUseCase
import com.side.runwithme.R
import com.side.runwithme.util.FASTEST_LOCATION_UPDATE_INTERVAL
import com.side.runwithme.util.LOCATION_UPDATE_INTERVAL
import com.side.runwithme.util.NOTIFICATION_CHANNEL_ID
import com.side.runwithme.util.NOTIFICATION_CHANNEL_NAME
import com.side.runwithme.util.NOTIFICATION_ID
import com.side.runwithme.util.RUNNING_STATE
import com.side.runwithme.util.TIMER_UPDATE_INTERVAL
import com.side.runwithme.util.TrackingUtility
import com.side.runwithme.util.TrackingUtility.Companion.getTTSTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

typealias PolyLine = MutableList<Location>

const val SERVICE_NOTSTART = 0
const val SERVICE_ISRUNNING = 1

@AndroidEntryPoint
class RunningService : LifecycleService() {

    private var tts: TextToSpeech? = null

    // Binder
    private val binder = LocalBinder()

    companion object {
        // 서비스 종료 여부
        var serviceState = SERVICE_NOTSTART // 앱 강제 종료 후 재시작시, SERVICE_ISRUNNING 상태이면 재시작
    }

    // NotificationCompat.Builder 주입
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    // NotificationCompat.Builder 수정하기 위함, 없으면 notification 삭제 안됨
    lateinit var currentNotificationBuilder: NotificationCompat.Builder

    // FusedLocationProviderClient 주입
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var getTTSOptionUseCase: GetTTSOptionUseCase

    private val runningState = MutableLiveData<RUNNING_STATE>()

    // 알림창에 표시될 시간
    private val timeRunInSeconds = MutableLiveData<Long>()

    private var lapTime = 0L // 시작 후 측정한 시간
    private var totalTime = 0L // 정지 시 저장되는 시간
    private var timeStarted = 0L // 측정 시작된 시간
    private var lastSecondTimestamp = 0L // 1초 단위 체크를 위함
    private var notMovingCount = 0 // 움직이지 않을 때마다 count, 연속 2번 count되면 pause
    private var tooFastCount = 0 // 속도가 너무 빠른 비정상 움직임 경우 count, 연속 2번 count되면 pause

    // 러닝을 시작하거나 다시 시작한 시간
    private var _startTime = 0L
    val startTime get() = _startTime

    private var _startDay = ""
    val startDay get() = _startDay

    private var pauseLast = false
    var stopRunningBeforeRegister = false // 정지 버튼을 누르고 서버에 기록 등록하기 전 상태 (오류 시 다시 등록하기 위함)

    private var pauseLatLng: Location? = null
    private var isResumeAfterStop: Boolean = false

    private val _isRunning = MutableLiveData<Boolean>() // 위치 추적 상태 여부
    val isRunning: LiveData<Boolean> get() = _isRunning

    private val _pathPoints = MutableLiveData<PolyLine>() // LatLng = 위도, 경도
    val pathPoints: LiveData<PolyLine> get() = _pathPoints

    private val _timeRunInMillis = MutableLiveData<Long>() // 뷰에 표시될 시간
    val timeRunInMillis: LiveData<Long> get() = _timeRunInMillis

    private val _sumDistance = MutableLiveData<Float>(0f)
    val sumDistance: LiveData<Float> get() = _sumDistance

    private val _errorEvent = MutableLiveData<Boolean>(false)
    val errorEvent: LiveData<Boolean> get() = _errorEvent

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

        initTextToSpeech()

        postInitialValues()

        initObserve()

        distanceTTS()
    }

    private fun initTextToSpeech() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (getTTSOptionUseCase().first()) {
                tts = TextToSpeech(this@RunningService) {
                    if (it == TextToSpeech.SUCCESS) {
                        val result = tts?.setLanguage(Locale.KOREAN)
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            showToast(resources.getString(R.string.not_supported_tts_language))
                            return@TextToSpeech
                        }
                    } else {
                        Firebase.crashlytics.recordException(IllegalArgumentException("음성 안내 오류"))
                    }
                }
            }
        }
    }

    private fun ttsSpeakAndVibrate(strTTS: String) {
        vibrate() // 1초간 진동

        if (tts == null) {
            return
        }
        tts?.speak(strTTS, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            val vibrator = getSystemService(Vibrator::class.java)
            vibrator.vibrate(1000L)
        } else {
            val vibrator =
                (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            val pattern = VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(pattern)
        }
    }

    private fun distanceTTS() {
        var count = 1
        sumDistance.observe(this) {
            val currentKM = it / 1000
            if (currentKM >= count) {
                ttsSpeakAndVibrate("${currentKM.toInt()} km 를 돌파했습니다. ${getTTSTime(timeRunInMillis.value!!)} 가 경과했습니다.")
                count++
            }
        }
    }

    private fun initObserve() {
        // 위치 추적 상태가 되면 업데이트 호출
        isRunning.observe(this) {
            updateLocation(it)
            updateNotificationTrackingState(it)
        }

        runningState.observe(this){
            when(it){
                RUNNING_STATE.START -> {
                    startTimer()
                    startTimerJob()
                    startForegroundService()
                    _startDay = LocalDate.now().toString()
                    ttsSpeakAndVibrate(it.message)
                    _isRunning.postValue(true)
                }
                RUNNING_STATE.RESUME -> {
                    startTimer()
                    startTimerJob()
                    ttsSpeakAndVibrate(it.message)
                    _isRunning.postValue(true)
                }
                RUNNING_STATE.PAUSE -> {
                    _isRunning.postValue(false)
                }
                RUNNING_STATE.STOP -> {
                    killService()
                    _isRunning.postValue(false)
                }
                RUNNING_STATE.FIRST_SHOW -> {
                    updateLocation(true)
                }
                else -> {

                }
            }
        }
    }

    // 초기화
    private fun postInitialValues() {
        _pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        _timeRunInMillis.postValue(0L)
    }

    private fun startTimer() {
        _startTime = System.currentTimeMillis()
        timeStarted = System.currentTimeMillis()
    }


    private val ISRUNNING_STATE = setOf(RUNNING_STATE.FIRST_SHOW, RUNNING_STATE.RESUME, RUNNING_STATE.START)
    private fun startTimerJob() {
        lifecycleScope.launch(Dispatchers.Main) {
            // 러닝 중 일 때
            while (runningState.value in ISRUNNING_STATE) {
                // 현재 시간 - 시작 시간 => 경과한 시간
                lapTime = System.currentTimeMillis() - timeStarted
                // 총시간 (일시정지 시 저장된 시간) + 경과시간 전달
                _timeRunInMillis.postValue(totalTime + lapTime)
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
        if (stopRunningBeforeRegister) {
            return
        }

        if (isTracking and TrackingUtility.hasLocationPermissions(this)) {
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_UPDATE_INTERVAL
            ).setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(FASTEST_LOCATION_UPDATE_INTERVAL)
                .setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL)
                .build()
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        // Tracking 권한이 없을 때 Service, RunningActivity 종료
        if (!TrackingUtility.hasLocationPermissions(this)) {
            ttsSpeakAndVibrate(resources.getString(R.string.not_supported_location))
            _errorEvent.postValue(true)
            killService()
        }
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {

            super.onLocationResult(result)

            result?.locations?.let { locations ->
                for(location in locations){
                    when(runningState.value){
                        // 처음 시작할 때
                        RUNNING_STATE.FIRST_SHOW -> {
                            pauseLatLng = location
                            addPathPoint(location)
                        }
                        RUNNING_STATE.START -> {
                            pauseLatLng = location
                            addPathPoint(location)
                        }
                        // 러닝 중
                        RUNNING_STATE.RESUME -> {
                            addPathPoint(location)
                        }
                        // 일시 정지 시
                        RUNNING_STATE.PAUSE -> {
                            if(checkResume(location)){
                                resumeRunningWhenDetectMove()
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun checkResume(lastLocation: Location): Boolean {
        if (stopRunningBeforeRegister) {
            return false
        }

        val distance = pauseLatLng!!.distanceTo(lastLocation)

        // 이동이 없어 중지 상태일 때, 8m 이동하면 다시 시작 시킴
        val isMoving = distance > 8f && ((System.currentTimeMillis() - _startTime) > 3000L)

        return isMoving
    }

    private fun resumeRunningWhenDetectMove(){
        runningState.postValue(RUNNING_STATE.RESUME)
        showToast(resources.getString(R.string.running_resume_when_moving))
        ttsSpeakAndVibrate(resources.getString(R.string.running_resume_when_moving))
        pauseLast = false
    }


    //위치 정보 추가
    private fun addPathPoint(location: Location?) {
        location?.let {
            pathPoints.value?.apply {
                // GPS가 튀어 정확하지 않거나 비정상적인 빠르기는 거리에 계산되지 않도록
                if (!isWrongGps(it)) return

                // 거리는 모두 계산
                distancePolyline(it)

                // 경로 최적화
                if (pathPoints.value!!.size >= 2) {
                    optimizationPolyLine(it)
                }
                add(it)
                _pathPoints.postValue(this)
            }
        }
    }

    private fun isWrongGps(willLatLng: Location): Boolean {
        // 일시 정지 후 재시작한 경우 -> 일시정지 지점에서 재시작점까지의 거리 계산하지 않기
        if(isResumeAfterStop){
            isResumeAfterStop = false
            return true
        }

        val polylines = pathPoints.value!!
        if (polylines.size < 1) return true

        val lastLatLng = polylines.last() // 마지막 좌표

        val distance = lastLatLng.distanceTo(willLatLng) // 추가할 좌표와의 거리 비교

        // 4초 동안 120m 이상 이동한 경우 = GPS 튀는 현상
        // GPS가 튀고 난 후 새로 추가될 때는 8초 동안 120m 이상 이동한 경우로 측정됨 (시속 42km 이기 때문에 비정상적으로 판단)
        // 비정상적인 빠르기
        if (distance > 120) {
            return false
        }

        return true
    }

    // 경로 최적화 알고리즘
    // 좌표 2개 이상일 때 추가할 좌표와 마지막 2개의 좌표를 비교하여
    // 거리가 가깝거나 가는 방향에서 각도가 완만한 경우 좌표 삭제
    private fun optimizationPolyLine(next: Location) {
        val polyLine = pathPoints.value
        val secondFromLast = polyLine!!.get(polyLine.size - 2)
        val last = polyLine.last()

        val lastToNextDistance = last.distanceTo(next)

        // 10미터 미만으로 너무 가까운 점이면 삭제
        if (lastToNextDistance < 10) {
            polyLine.remove(last)
            return
        }

        // 200미터 이상으로 너무 먼 점이면 각도 상관없이 무조건 저장
        if (lastToNextDistance >= 200) return

        // 적당한 거리라면 각도 비교
        val v1Bearing = secondFromLast.bearingTo(last)
        val v2Bearing = last.bearingTo(next)
        val diffBearing = Math.abs(Math.abs(v1Bearing) - Math.abs(v2Bearing))
        val checkBearing: Float = if(lastToNextDistance < 30){
            5F
        }else if(lastToNextDistance < 50){
            4F
        }else if(lastToNextDistance < 100){
            3F
        }else{
            1.5F
        }

        // lastDistance에 따라서 각도 차이(diffBearing)이 checkBearing보다 낮거나 같은 경우 좌표 삭제(거의 직선인 경우)
        if(diffBearing <= checkBearing){
            polyLine.remove(last)
        }
    }

    // 4초 이상 이동했는데 이동거리가 2.5m 이하인 경우가 연속 2번인 경우 정지하고, 마지막 위치를 기록함 (최소 오차 3초)
    private fun checkNotMoving(distance: Float): Boolean {
        val isNotMoving = distance < 2.5f && (System.currentTimeMillis() - startTime) > 3000L
        if (isNotMoving) {
            notMovingCount += 1

            if (notMovingCount >= 2) {
                notMovingCount = 0
                return true
            }
        } else {
            notMovingCount = 0
        }

        return false
    }

    private fun stopWhenNotMoving(lastLatLng: Location){
        isResumeAfterStop = true
        pauseLatLng = lastLatLng
        pauseLast = true
        pauseService(resources.getString(R.string.running_pause_not_moving), resources.getString(R.string.running_pause_not_moving))
    }

    // 4초 이상 이동했는데 이동거리가 55m 이상인 경우가 연속 2번인 경우 정지 (최소 오차 3초) -> 너무 빠른 경우
    private fun checkFastMoving(distance: Float): Boolean {
        val isFastMoving = distance > 55f && (System.currentTimeMillis() - startTime) > 3000L
        if (isFastMoving) {
            tooFastCount += 1

            if (tooFastCount >= 2) {
                tooFastCount = 0
                return true
            }
        } else {
            tooFastCount = 0
        }

        return false
    }

    private fun stopWhenFastMoving(){
        isResumeAfterStop = true
        pauseService(resources.getString(R.string.running_pause_too_fast), resources.getString(R.string.running_pause_too_fast))
    }

    // 거리 표시 (마지막 전, 마지막 경로 차이 비교)
    private fun distancePolyline(next: Location) {
        // 일시 정지 후 재시작한 경우 -> 일시정지 지점에서 재시작점까지의 거리 계산하지 않기
        if(isResumeAfterStop){
            isResumeAfterStop = false
            return
        }

        val polylines = pathPoints.value!!
        val possiblePolyline = polylines.isNotEmpty()

        if (possiblePolyline) {
            val lastLatLng = polylines.last() // 마지막 경로

            val distance = lastLatLng.distanceTo(next)

            /** 움직임 멈춤 체크를 다른 곳에 둬야할듯 **/
            if(checkNotMoving(distance)){
                stopWhenNotMoving(lastLatLng)
                return
            }

            if(checkFastMoving(distance)){
                stopWhenFastMoving()
                return
            }

            _sumDistance.postValue(sumDistance.value!!.plus(distance))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                // 시작 되었을 때
                RUNNING_STATE.START.name -> {
                    runningState.postValue(RUNNING_STATE.START)
                }
                // 재개 되었을 때
                RUNNING_STATE.RESUME.name -> {
                    runningState.postValue(RUNNING_STATE.RESUME)
                }
                // 일시정지 되었을 때
                RUNNING_STATE.PAUSE.name -> {
                    pauseService(resources.getString(R.string.running_pause_message), RUNNING_STATE.PAUSE.message)
                    runningState.postValue(RUNNING_STATE.PAUSE)
                }
                // 종료 되었을 때
                RUNNING_STATE.STOP.name -> {
                    runningState.postValue(RUNNING_STATE.STOP)
                }
                // 처음 화면 켰을 때
                RUNNING_STATE.FIRST_SHOW.name -> {
                    runningState.postValue(RUNNING_STATE.FIRST_SHOW)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    // 서비스 정지
    private fun pauseService(toastMsg: String, ttsMsg: String) {
        showToast(toastMsg)
        ttsSpeakAndVibrate(ttsMsg)
        runningState.postValue(RUNNING_STATE.PAUSE)
    }

    // 서비스가 종료되었을 때
    private fun killService() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        showToast(resources.getString(R.string.running_stop_message))
        ttsSpeakAndVibrate(resources.getString(R.string.running_stop_message))
        serviceState = SERVICE_NOTSTART
        stopSelf()
    }

    // Notification 등록, 서비스 시작
    private fun startForegroundService() {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForeground(
                NOTIFICATION_ID, baseNotificationBuilder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        }

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
        val notificationActionText = if (isTracking) resources.getString(R.string.running_pause) else resources.getString(R.string.running_resume)
        // 정지 or 시작 버튼 클릭 시 그에 맞는 액션 전달
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, RunningService::class.java).apply {
                action = RUNNING_STATE.PAUSE.name
            }
            // pending Intent 객체 생성
            PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            val resumeIntent = Intent(this, RunningService::class.java).apply {
                action = RUNNING_STATE.RESUME.name
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

    override fun onDestroy() {

        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }

        serviceState = SERVICE_NOTSTART

        super.onDestroy()
    }

}