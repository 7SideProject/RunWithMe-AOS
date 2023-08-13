package com.side.runwithme.di

import android.app.Application
import com.side.runwithme.exception.bearer.BearerExceptionHandler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass: Application() {

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler(BearerExceptionHandler(this))
    }

}


