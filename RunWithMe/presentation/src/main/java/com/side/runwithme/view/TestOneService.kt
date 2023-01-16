package com.side.runwithme.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.side.runwithme.R
import com.side.runwithme.util.*
import com.side.runwithme.view.TestOneService.Companion.runTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestOneService : LifecycleService() {

    private var timeStarted = 0L
    private var lapTime = 0L
    private var totalTime = 0L
    private var lastSecondTimeStamp = 0L
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationBuilder: NotificationCompat.Builder

    companion object {
        val isTracking = MutableLiveData<Boolean>(false)
        val runTime = MutableLiveData(0L)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START_SERVICE -> {
                startForegroundService()
                startTimer()
            }
            ACTION_RESUME_SERVICE ->{
                updateNotification(true)
                isTracking.postValue(true)
                startTimer()
            }
            ACTION_PAUSE_SERVICE -> {
                updateNotification(false)
                isTracking.postValue(false)
            }
            ACTION_STOP_SERVICE -> {
                isTracking.postValue(false)
                stopSelf()
            }

        }

        isTracking.observe(this){
            updateNotification(isTracking.value!!)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer(){
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        notificationChannel = NotificationChannel(
            "test",
            "test",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        notificationBuilder =
            NotificationCompat.Builder(this, notificationChannel.id)
                .setSmallIcon(R.drawable.ic_launcher_background)

        startForeground(1, notificationBuilder.build())

        runTime.observe(this){
            notificationBuilder.setContentText(runningTimeFormatter(it))
            notificationManager.notify(1, notificationBuilder.build())
        }

    }

    private fun updateNotification(isTracking: Boolean){
        var intent: Intent? = null
        var pendingIntent: PendingIntent?
        var actionText = ""

        if(isTracking){
            intent = Intent(this, TestOneService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_MUTABLE)
            actionText = "일시정지"
        }else{
            intent = Intent(this, TestOneService::class.java).apply {
                action = ACTION_RESUME_SERVICE
            }
            pendingIntent = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_MUTABLE)
            actionText = "다시 시작"
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


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