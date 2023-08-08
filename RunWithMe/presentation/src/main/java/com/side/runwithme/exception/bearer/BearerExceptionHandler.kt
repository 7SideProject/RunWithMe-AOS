package com.side.runwithme.exception.bearer

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.gun0912.tedpermission.TedPermissionActivity
import com.side.runwithme.view.login.LoginActivity

class BearerExceptionHandler(
    application: Application
) : Thread.UncaughtExceptionHandler {

    private var lastActivity: Activity? = null
    private var activityCount = 0

    init {
        application.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    Log.d("test123", "Application onActivityCreated: ${activity}")
                    if(activity is TedPermissionActivity) return
                    lastActivity = activity
                }

                override fun onActivityStarted(activity: Activity) {
                    if(activity is TedPermissionActivity) return
                    activityCount++
                    lastActivity = activity
                }

                override fun onActivityResumed(activity: Activity) {

                }

                override fun onActivityPaused(activity: Activity) {
                }

                override fun onActivityStopped(activity: Activity) {
                    if(activity is TedPermissionActivity) return
                    activityCount--
                    if (activityCount < 0) {
                        lastActivity = null
                    }

                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

                }

                override fun onActivityDestroyed(activity: Activity) {

                }
            })
    }
    override fun uncaughtException(t: Thread, e: Throwable) {

        if(e.cause?.cause is BearerException){
            val intent = Intent(lastActivity, LoginActivity::class.java).apply {
                putExtra("BearerError", "로그인을 다시 해주세요.")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            lastActivity?.apply {
                startActivity(intent)
                finish()
            }

            System.exit(-1)
        }
    }
}