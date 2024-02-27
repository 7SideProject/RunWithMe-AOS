package com.side.runwithme.di

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.side.runwithme.BuildConfig
import com.side.runwithme.exception.bearer.BearerExceptionHandler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass: Application() {

    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAOAPIKEY)

        setBearerExceptionHandler()
    }

    private fun setBearerExceptionHandler(){
        val bearerExceptionHandler = Thread.getDefaultUncaughtExceptionHandler() ?: return
        Thread.setDefaultUncaughtExceptionHandler(
            BearerExceptionHandler(this, bearerExceptionHandler
            )
        )

    }

}


