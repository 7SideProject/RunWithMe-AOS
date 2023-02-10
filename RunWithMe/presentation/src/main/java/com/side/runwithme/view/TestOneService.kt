package com.side.runwithme.view

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.MultipartPathOverlay
import com.naver.maps.map.overlay.PathOverlay
import com.side.runwithme.R
import com.side.runwithme.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.daum.android.map.coord.MapCoordLatLng
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import javax.inject.Inject


@AndroidEntryPoint
class TestOneService : LifecycleService() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var notificationManager: NotificationManager

    private var timeStarted = 0L
    private var lapTime = 0L
    private var totalTime = 0L
    private var lastSecondTimeStamp = 0L
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationBuilder: NotificationCompat.Builder


    companion object {
        val allPositionList = MutableLiveData<MutableList<MutableList<LatLng>>>(
            mutableListOf(
                mutableListOf()
            )
        )
        val positionList = MutableLiveData<MutableList<LatLng>>(mutableListOf())
        val isTracking = MutableLiveData<Boolean>(false)
        val runTime = MutableLiveData(0L)
        val pathPoints = MutableLiveData<MultipartPathOverlay>()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                startForegroundService()
                startTimer()
                initPathPoints()
            }
            ACTION_RESUME_SERVICE -> {
                Log.d("test123", "onStartCommand: RESUME")
                updateNotification(true)
                isTracking.postValue(true)
                addEmptyPolyLine()
                startTimer()

            }
            ACTION_PAUSE_SERVICE -> {
                positionList.value = mutableListOf()
                updateNotification(false)
                isTracking.postValue(false)
            }
            ACTION_STOP_SERVICE -> {
                isTracking.postValue(false)
                stopSelf()
            }

        }

        isTracking.observe(this) {
            updateLocation(it)
            updateNotification(it)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun initPathPoints() {

        positionList.observe(this) {
            if (it.size == 2) {
                Log.d("test123", "initPathPoints: ")

                it.forEach { l ->
                    allPositionList.value = allPositionList.value!!.apply {
                        this.last().add(l)
                    }
                }


//                val mpo = MultipartPathOverlay()
//                mpo.coordParts = listOf(positionList.value)
//                pathPoints.postValue(mpo)
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(isTracking: Boolean) {
        if (isTracking) {
            if (hasLocationPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL // 위치 업데이트 주기
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL // 가장 빠른 위치 업데이트 주기
                    priority =
                        LocationRequest.PRIORITY_HIGH_ACCURACY // 배터리소모를 고려하지 않으며 정확도를 최우선으로 고려
                    maxWaitTime = LOCATION_UPDATE_INTERVAL // 최대 대기시간
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
//            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            if (isTracking.value!!) {
                p0?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Log.d("test123", "${location.latitude} ${location.longitude} ")
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location) {
        val position = LatLng(location.latitude, location.longitude)

        positionList.value = positionList.value!!.apply {
            this.add(position)
        }

        if (positionList.value!!.size < 2) {
            return
        }

        Log.d("test123", "addPathPoint1: ${positionList.value}")


        allPositionList.value = allPositionList.value!!.apply {
            this.last().add(position)
        }

        Log.d("test123", "addPathPoint2: ${pathPoints.value}")
    }


    private fun startTimer() {

        timeStarted = System.currentTimeMillis()
        isTracking.value = true

        CoroutineScope(Dispatchers.IO).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                runTime.postValue(totalTime + lapTime)
                // 알림창에 표시될 시간 초 단위로 계산함
                if (runTime.value!! >= lastSecondTimeStamp + 1000L) {
                    runTime.postValue(runTime.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(50L)
            }
            totalTime += lapTime
        }
    }

    private fun addEmptyPolyLine() {

        allPositionList.value!!.add(mutableListOf())
//        pathPoints.value = pathPoints.value?.apply {
//            this.coordParts.add(listOf())
//        }
        Log.d("test123", "addEmptyPolyLine: ${pathPoints.value}")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        notificationChannel = NotificationChannel(
            "test",
            "test",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        notificationBuilder =
            NotificationCompat.Builder(this, notificationChannel.id)
                .setSmallIcon(R.drawable.ic_launcher_background)

        startForeground(1, notificationBuilder.build())

        runTime.observe(this) {
            notificationBuilder.setContentText(runningTimeFormatter(it))
            notificationManager.notify(1, notificationBuilder.build())
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNotification(isTracking: Boolean) {
//        Log.d("test123", "updateNotification: ")
        var intent: Intent? = null
        var pendingIntent: PendingIntent?
        var actionText = ""

        if (isTracking) {
            intent = Intent(this, TestOneService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_MUTABLE)
            actionText = "일시정지"
        } else {
            intent = Intent(this, TestOneService::class.java).apply {
                action = ACTION_RESUME_SERVICE
            }
            pendingIntent = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_MUTABLE)
            actionText = "다시 시작"
        }

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        notificationBuilder =
            NotificationCompat.Builder(this, notificationChannel.id)
                .setSmallIcon(R.drawable.ic_launcher_background)

        notificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(notificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        notificationBuilder.addAction(
            R.drawable.ic_launcher_background,
            actionText,
            pendingIntent
        )
        notificationManager.notify(1, notificationBuilder.build())

    }

}