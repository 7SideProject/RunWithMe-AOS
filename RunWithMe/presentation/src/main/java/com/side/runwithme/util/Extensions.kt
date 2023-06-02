package com.side.runwithme.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Insets
import android.graphics.Point
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import decrypt
import encrypt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

fun getDeviceSize(activity: Activity): Point {
    val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return windowManager.currentWindowMetricsPointCompat()
}

// 다이얼로그 사이즈 조절
fun Context.dialogResize(dialog: Dialog, width: Float, height: Float){
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    if (Build.VERSION.SDK_INT < 30){
        val display = windowManager.defaultDisplay
        val size = Point()

        display.getSize(size)

        val window = dialog.window

        val x = (size.x * width).toInt()
        val y = (size.y * height).toInt()

        window?.setLayout(x, y)

    }else{
        val rect = windowManager.currentWindowMetrics.bounds

        val window = dialog.window
        val x = (rect.width() * width).toInt()
        val y = (rect.height() * height).toInt()

        window?.setLayout(x, y)
    }
}

fun WindowManager.currentWindowMetricsPointCompat(): Point {
    // R(30) 이상
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val windowInsets = currentWindowMetrics.windowInsets
        var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
        windowInsets.displayCutout?.run {
            insets = Insets.max(
                insets,
                Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom)
            )
        }
        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom
        Point(
            currentWindowMetrics.bounds.width() - insetsWidth,
            currentWindowMetrics.bounds.height() - insetsHeight
        )
    } else {
        Point().apply {
            defaultDisplay.getSize(this)
        }
    }
}

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}

// DataStore 확장 함수
object preferencesKeys {
    val JWT = stringPreferencesKey("jwt")
    val REFRESH_TOKEN = stringPreferencesKey("refresh-token")
    val EMAIL = stringPreferencesKey("email")
    val SEQ = stringPreferencesKey("seq")
    val WEIGHT = intPreferencesKey("weight")
}

suspend fun <T> DataStore<Preferences>.saveValue(key: Preferences.Key<T>, value: T) {
    edit { prefs -> prefs[key] = value }
}

// 암호화 적용
suspend fun DataStore<Preferences>.saveEncryptStringValue(key: Preferences.Key<String>, value: String) {
    edit { prefs -> prefs[key] = encrypt(value) }
}

suspend fun <T> DataStore<Preferences>.getValue(key: Preferences.Key<T>, type: Int): Flow<Any> {
    return data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[key] ?: when (type) {
                KEY_INT -> {
                    0
                }
                KEY_BOOLEAN -> {
                    false
                }
                KEY_STRING -> {
                    ""
                }
                else -> {}
            }
        }
}

// 암호화 적용
suspend fun DataStore<Preferences>.getDecryptStringValue(key: Preferences.Key<String>): Flow<Any> {
    return data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            decrypt(prefs[key] ?: "")
        }
}