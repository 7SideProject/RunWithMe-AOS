package com.side.runwithme.view


import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.drawToBitmap
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityTestOneBinding
import com.side.runwithme.util.*
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPointBounds
import net.daum.mf.map.api.MapView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


private const val TAG ="TestOneActivity"
class TestOneActivity : BaseActivity<ActivityTestOneBinding>(R.layout.activity_test_one){
    private var isPause = false
    private lateinit var mapView: MapView
    private lateinit var surfaceView: GLSurfaceView
    override fun init() {

        requestPermission(onSuccess = {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_DENIED
            ) {
                permissionDialog()
            }
        }, onFailed = { showToast("권한을 허용해주세요") })



//        mapView = MapView(this)
//        binding.mapView.addView(mapView)

        initClickListener()
        TestOneService.runTime.observe(this) {
            binding.tvTotalTime.text = runningTimeFormatter(it)
        }

        TestOneService.pathPoints.observe(this){
            Log.d("test123", "init: ${it.mapPoints}")
            val mapPointBounds = MapPointBounds(it.mapPoints)
            val padding = 100 // px
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding))
            mapView.addPolyline(it)
        }

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
////                val bitmap = Bitmap.createBitmap(mapView.width, mapView.height, Bitmap.Config.ARGB_8888)
//                val bitmap = mapView.drawToBitmap()
//                val holder = this@TestOneActivity.mapView.holder
////                val canvas = holder.lockCanvas()
//                val canvas =Canvas(bitmap)
//                Log.d(TAG, "initClickListener: ${canvas}")
//                mapView.draw(canvas)
//                test.setImageBitmap(bitmap)
//                test.bringToFront()
//                Log.d(TAG, "initClickListener: ${bitmap.height}")
            }
        }
    }

    private fun sendServiceAction(action: String) {
        val intent = Intent(this@TestOneActivity, TestOneService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }


}