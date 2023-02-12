package com.side.runwithme.view


import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.MultipartPathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.base.BaseActivity
import com.side.runwithme.databinding.ActivityTestOneBinding
import com.side.runwithme.util.*


private const val TAG = "TestOneActivity"

class TestOneActivity : BaseActivity<ActivityTestOneBinding>(R.layout.activity_test_one),
    OnMapReadyCallback {
    private var isPause = false

    private lateinit var mapFragment: MapFragment
    private lateinit var surfaceView: GLSurfaceView
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var marker: Marker


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun init() {

        initRequestPermission()
        initNaverMap()
        initMarker()
        initClickListener()
        observeRunningTime()

    }

    private fun observeRunningTime() {
        TestOneService.runTime.observe(this) {
            binding.tvTotalTime.text = runningTimeFormatter(it)
        }
    }

    private fun initMarker() {
        marker = Marker()
    }

    private fun initRequestPermission() {
        requestPermission(onSuccess = {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_DENIED
            ) {
                permissionDialog()
            }
        }, onFailed = { showToast("권한을 허용해주세요") })
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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")

        val listener = DialogInterface.OnClickListener { _, p1 ->
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

                setLatLngBounds()

                naverMap.takeSnapshot {
                    test.setImageBitmap(it)
                }

            }
        }
    }

    private fun setLatLngBounds() {
        val latlng = TestOneService.allPositionList.value

        val bound = LatLngBounds.Builder()
        latlng!!.forEach {
            it.forEach { latlng ->
                bound.include(latlng)
            }
        }

        val boundBuilder = bound.build()
        naverMap.moveCamera(CameraUpdate.fitBounds(boundBuilder, 400))
    }

    private fun sendServiceAction(action: String) {
        val intent = Intent(this@TestOneActivity, TestOneService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        setCurrentPosition()
        getPositionList()

    }

    private fun getPositionList() {
        TestOneService.allPositionList.observe(this) { latLngList ->
            if (latLngList.last().isEmpty()) {
                return@observe
            }

            Log.d("test123", "onMapReady: $latLngList")

            naverMap.moveCamera(CameraUpdate.scrollTo(latLngList.last().last()))

            moveMarker(latLngList)

            if (latLngList.last().size < 2) {
                return@observe
            }

            drawPolyLine(latLngList)

        }
    }

    private fun drawPolyLine(latLngList: MutableList<MutableList<LatLng>>) {
        val mpo = MultipartPathOverlay()
        mpo.coordParts = latLngList
        mpo.colorParts =
            listOf(
                MultipartPathOverlay.ColorPart(
                    Color.RED, Color.WHITE, Color.GRAY, Color.LTGRAY
                )
            )
        mpo.map = naverMap
    }

    private fun moveMarker(latLngList: MutableList<MutableList<LatLng>>) {
        marker.position = latLngList.last().last()
        marker.map = naverMap
    }

    private fun setCurrentPosition() {
        locationSource = FusedLocationSource(this, 1000)
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        naverMap.addOnLocationChangeListener {
            Log.d("test123", "init: ${it.latitude} ${it.longitude}")
            naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude)))
            naverMap.locationTrackingMode = LocationTrackingMode.None

            marker.position = LatLng(it.latitude, it.longitude)
            marker.map = naverMap
        }
    }


}