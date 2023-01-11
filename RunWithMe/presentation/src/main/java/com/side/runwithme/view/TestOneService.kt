package com.side.runwithme.view

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestOneService : LifecycleService() {

    private var timeStarted = 0L
    private var lapTime = 0L
    private var totalTime = 0L
    private var lastSecondTimeStamp = 0L

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val runTime = MutableLiveData(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        timeStarted = System.currentTimeMillis()
        isTracking.postValue(true)

        CoroutineScope(Dispatchers.Main).launch {
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
        return super.onStartCommand(intent, flags, startId)


    }
}