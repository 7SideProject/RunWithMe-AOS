package com.side.runwithme.service

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.side.runwithme.util.ACTION_START_OR_RESUME_SERVICE
import com.side.runwithme.util.ACTION_STOP_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.daum.android.map.coord.MapCoordLatLng
import javax.inject.Inject

typealias PolyLine = MutableList<MapCoordLatLng>
typealias Polylines = MutableList<PolyLine>

@AndroidEntryPoint
class RunningService : LifecycleService() {

    // 서비스 종료 여부
    private var serviceKilled = false

    // NotificationCompat.Builder 주입
//    @Inject
//    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    // NotificationCompat.Builder 수정하기 위함
    lateinit var currentNotificationBuilder : NotificationCompat.Builder

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

    companion object{
        val isTracking = MutableLiveData<Boolean>() // 위치 추적 상태 여부
        val pathPoints = MutableLiveData<Polylines>() // LatLng = 위도, 경도
        val timeRunInMillis = MutableLiveData<Long>() // 뷰에 표시될 시간
        val isFirstRun = false // 처음 실행 여부 (false = 실행되지않음)
        val sumDistance = MutableLiveData<Float>(0f)
        val defaultLatLng = MutableLiveData<MapCoordLatLng>()  // 시작 지점인듯?
    }

    private fun initTextToSpeech() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Toast.makeText(this, "음성 안내를 지원하지 않는 버전입니다.", Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("dddd", "onCreate: ")
        postInitialValues()

        // 위치 추적 상태가 되면 업데이트 호출
        isTracking.observe(this){

        }
    }

    // 초기화
    private fun postInitialValues(){
        isTracking.postValue(false)
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    private fun startTimer(){
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
                Log.d("dddd", "lapTime: ${lapTime}")
                Log.d("dddd", "timeRunInMillis: ${timeRunInMillis.value}")
                delay(50L)
            }

            // 위치 추적이 종료(정지) 되었을 때 총시간 저장
            totalTime += lapTime
        }
    }

    // 서비스가 종료되었을 때
    private fun killService(){
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

    // 서비스가 호출 되었을 때
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("dddd", "onStartCommand: service")
        Log.d("dddd", "onStartCommand: action : ${intent?.action}")

        intent?.let{
            when(it.action){
                // 시작, 재개 되었을 때
                ACTION_START_OR_RESUME_SERVICE ->{
                    startTimer()
                    startTime = System.currentTimeMillis()
                }
                // 종료 되었을 때
                ACTION_STOP_SERVICE ->{
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    // 서비스 정지
    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

}