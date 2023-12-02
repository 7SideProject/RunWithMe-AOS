package com.side.runwithme.module

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.side.runwithme.R
import com.side.runwithme.util.NOTIFICATION_CHANNEL_ID
import com.side.runwithme.view.running.RunningActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    //FusedLocationProviderClient DI
    @ServiceScoped
    @Provides
    fun providesFusedLocationProviderClient(
        @ApplicationContext context:Context
    ) = FusedLocationProviderClient(context)

    // PendingIntent DI
    @RequiresApi(Build.VERSION_CODES.S)
    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext context : Context
    ) = PendingIntent.getActivity(
        context, 0,
        Intent(context, RunningActivity::class.java).also{
            it.action = Intent.ACTION_MAIN
            it.addCategory(Intent.CATEGORY_LAUNCHER) // 액티비티가 어플리케이션의 런처에 표시되고 태스크의 첫 액티비티가 될 수 있다.
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, PendingIntent.FLAG_MUTABLE
    )!!

    // NotificationCompat.Builder DI
    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)   // 자동 꺼짐 false
        .setOngoing(true)       // 유지
        .setSmallIcon(R.drawable.rwm_logo)
        .setContentTitle("달리기 기록을 측정 중입니다.")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)
}
