package com.side.runwithme.view


import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.drawToBitmap
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.MultipartPathOverlay
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityTestOneBinding
import com.side.runwithme.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPointBounds
import net.daum.mf.map.api.MapView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


private const val TAG = "TestOneActivity"

class TestOneActivity : BaseActivity<ActivityTestOneBinding>(R.layout.activity_test_one),
    OnMapReadyCallback {
    private var isPause = false

    private lateinit var mapFragment: MapFragment
    private lateinit var surfaceView: GLSurfaceView


    override fun init() {


        requestPermission(onSuccess = {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_DENIED
            ) {
                permissionDialog()
            }
        }, onFailed = { showToast("권한을 허용해주세요") })

        initNaverMap()


        initClickListener()
        TestOneService.runTime.observe(this) {
            binding.tvTotalTime.text = runningTimeFormatter(it)
        }


    }

    private fun initNaverMap() {
        val fm = supportFragmentManager

        mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    private fun permissionDialog() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when (p1) {
                DialogInterface.BUTTON_POSITIVE -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 2
                )

            }
        }
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", null)

        builder.show()
    }


    fun initClickListener() {
        binding.apply {
            btnStart.setOnClickListener {
                sendServiceAction(ACTION_START_SERVICE)
                btnStop.visibility = View.VISIBLE
            }
            btnPause.setOnClickListener {
                if (isPause) {
                    sendServiceAction(ACTION_RESUME_SERVICE)
                } else {
                    sendServiceAction(ACTION_PAUSE_SERVICE)
                }
                isPause = !isPause
            }
            btnStop.setOnClickListener {
                sendServiceAction(ACTION_STOP_SERVICE)
            }
            btnCapture.setOnClickListener {

            }
        }
    }

    private fun sendServiceAction(action: String) {
        val intent = Intent(this@TestOneActivity, TestOneService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }

    override fun onMapReady(p0: NaverMap) {
        TestOneService.pathPoints.observe(this) {
            if (it != null) {
                Log.d("test123", "onMapReady: ${it.coordParts}")
                it.colorParts = listOf(
                    MultipartPathOverlay.ColorPart(
                        Color.RED, Color.WHITE, Color.GRAY, Color.LTGRAY),
                    MultipartPathOverlay.ColorPart(
                        Color.GREEN, Color.WHITE, Color.DKGRAY, Color.LTGRAY)
                )

                it.map = p0
                p0.moveCamera(CameraUpdate.scrollTo(it.coordParts.last().last()))


            }

//            if(it.last().coords.size > 2){
//                for(i in it.indices) {
//                    it[i].map = p0
//

//                }
//            }


        }

    }


}