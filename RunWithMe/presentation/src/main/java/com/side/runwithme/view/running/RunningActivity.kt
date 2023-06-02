package com.side.runwithme.view.running

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.example.seobaseview.base.BaseActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningBinding
import com.side.runwithme.service.PolyLine
import com.side.runwithme.service.RunningService
import com.side.runwithme.util.*
import com.side.runwithme.util.preferencesKeys.WEIGHT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.Math.round
import javax.inject.Inject

class RunningActivity : BaseActivity<ActivityRunningBinding>(R.layout.activity_running),
    OnMapReadyCallback {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    private lateinit var polyline: PathOverlay

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private var naverLatLng = mutableListOf<LatLng>()

    private lateinit var latLngBoundsBuilder: LatLngBounds.Builder

    private lateinit var runningService: RunningService

    private var type: String = GOAL_TYPE_TIME
    private var goal = 60 * 1000L
    private var weight = 70

    // 라이브 데이터 받아온 값들
    private var caloriesBurned: Int = 0
    private var sumDistance: Float = 0f
    private var currentTimeInMillis = 0L

    override fun init() {
        initLatLngBounds()

//        weight = sharedPref.getInt(USER_WEIGHT, 70)
//        type = sharedPref.getString(RUN_GOAL_TYPE, GOAL_TYPE_TIME)!!
        lifecycleScope.launch {
            weight = dataStore.getValue(WEIGHT, KEY_INT).first() as Int
        }


        initMapView()

        initClickListener()

        firstStart()

        initDrawLine()
    }

    private fun initLatLngBounds() {
        latLngBoundsBuilder = LatLngBounds.Builder()
    }

    private fun initClickListener() {
        binding.apply {
            ibStart.setOnClickListener {
                ibStart.visibility = View.GONE
                ibStop.visibility = View.GONE
                ibPause.visibility = View.VISIBLE

                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            }

            ibPause.setOnClickListener {
                ibStart.visibility = View.VISIBLE
                ibStop.visibility = View.VISIBLE
                ibPause.visibility = View.GONE

                sendCommandToService(ACTION_PAUSE_SERVICE)
            }

            ibStop.setOnClickListener {
                stopRun()
                takeSnapShot()
            }
        }
    }

    private fun takeSnapShot() {
        moveLatLngBounds()

        naverMap.takeSnapshot {
            // image 넘기기
//            binding.img.setImageBitmap(it)
        }
    }

    // 이동한 전체 polyLine 담기
    private fun moveLatLngBounds() {
        val bounds = latLngBoundsBuilder.build()
        naverMap.moveCamera(CameraUpdate.fitBounds(bounds, 300))
    }

    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun initDrawLine() {
        polyline = PathOverlay()
    }

    private fun initObserve() {

        // 좌표 observe
        runningService.pathPoints.observe(this) {
            if (it.isNotEmpty()) {

                val lat = it.last().latitude
                val lng = it.last().longitude
                val latlng = LatLng(lat, lng)

                naverLatLng.add(latlng)

                latLngBoundsBuilder.include(latlng)

                if (it.size >= 2) {
                    drawPolyline()
                }

                // 경로 최적화
                if (it.size >= 4) {
                    optimizationPolyLine()
                }

            }
        }

        // 시간(타이머) 경과 observe
        runningService.timeRunInMillis.observe(this) {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTimeSummery(it)
            changeTimeText(formattedTime)

            // 프로그래스바 진행도 변경
            if (it > 0 && type == GOAL_TYPE_TIME) {
                binding.progressBarGoal.progress =
                    if ((it / (goal / 100)).toInt() >= 100) 100 else (it / (goal / 100)).toInt()
            }
        }

        // 거리 observe
        runningService.sumDistance.observe(this) {
            sumDistance = it
            changeDistanceText(sumDistance)
            changeCalorie(sumDistance)

            // 프로그래스바 진행도 변경
            if (sumDistance > 0 && type == GOAL_TYPE_DISTANCE) {
                binding.progressBarGoal.progress =
                    if ((sumDistance / (goal / 100)).toInt() >= 100) 100 else (sumDistance / (goal / 100)).toInt()
            }
        }

        // 러닝 뛰고 있는 지 observe
        runningService.isRunning.observe(this) {
            if (it) { // 러닝을 뛰고 있는 경우
                binding.apply {
                    ibStart.visibility = View.GONE
                    ibStop.visibility = View.GONE
                    ibPause.visibility = View.VISIBLE
                }
            } else { // 일시 정지 된 경우
                binding.apply {
                    ibStart.visibility = View.VISIBLE
                    ibStop.visibility = View.VISIBLE
                    ibPause.visibility = View.GONE
                }
            }
        }

    }

    // 경로 최적화 알고리즘
    // 좌표 4개 이상일 때 이전 3개의 좌표를 비교하여
    // 거리가 가깝거나 가는 방향에서 각도가 완만한 경우 좌표 삭제
    private fun optimizationPolyLine() {
        val first = naverLatLng.get(naverLatLng.size - 4)
        val second = naverLatLng.get(naverLatLng.size - 3)
        val third = naverLatLng.get(naverLatLng.size - 2)

        val result = FloatArray(1)
        Location.distanceBetween(
            second.latitude,
            second.longitude,
            third.latitude,
            third.longitude,
            result
        )

        val lastDistance = result[0]

        // 10미터 미만으로 너무 가까운 점이면 삭제
        if (lastDistance < 10) {
            naverLatLng.removeAt(naverLatLng.size - 3)
            return
        }

        // 100미터 이상으로 너무 먼 점이면 각도 상관없이 무조건 저장
        if (lastDistance >= 100) return

        // 적당한 거리라면 각도 비교
        val v1: Vector = getVector(first, second) // 기존 벡터
        val v2: Vector = getVector(second, third) // 비교할 벡터

        val cosine = getVectorDotProduct(v1, v2) / (getVectorDistance(v1) * getVectorDistance(v2))

        // 거리가 가까울 땐 +- 5도 (거의 직선인 경우 삭제)
        if (lastDistance < 50) {
            if (cosine > Math.cos(deg2rad(20.0))) {
                naverLatLng.removeAt(naverLatLng.size - 3)
                return
            }
        } else { // 거리가 멀어지면 여유범위 줄이기
            if (cosine > Math.cos(deg2rad(10.0))) {
                naverLatLng.removeAt(naverLatLng.size - 3)
                return
            }

        }

    }

    // This function converts decimal degrees to radians
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private class Vector(var x: Double, var y: Double)

    // 두 좌표를 위도에 따른 경도의 거리를 적용해서 최대한 오차없는 평면벡터화 한 것
    private fun getVector(point1: LatLng, point2: LatLng): Vector {
        return Vector(
            (point2.latitude - point1.latitude) / Math.cos(deg2rad(point2.latitude)),
            point2.longitude - point1.longitude
        )
    }

    // 벡터의 크기
    private fun getVectorDistance(v: Vector): Double {
        return Math.sqrt(v.x * v.x + v.y * v.y)
    }

    // 벡터의 내적
    private fun getVectorDotProduct(v1: Vector, v2: Vector): Double {
        return v1.x * v2.x + v1.y * v2.y
    }

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

    private fun firstStart() {

        val runningLoadingDialog = RunningLoadingDialog(this)
        runningLoadingDialog.show()

//        lifecycleScope.launch(Dispatchers.Main) {
//            sendCommandToService(ACTION_SHOW_RUNNING_ACTIVITY)
//            delay(3000L)
//            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
//            // bindService
//            Intent(this@RunningActivity, RunningService::class.java).also { intent ->
//                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
//            }
//            delay(1000L)
//            runningLoadingDialog.dismiss()
//        }

        lifecycleScope.launch {
            startRun()
            runningLoadingDialog.dismiss()
        }

    }

    private suspend fun startRun() {
        sendCommandToService(ACTION_SHOW_RUNNING_ACTIVITY)
        delay(3000L)
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        // bindService
        Intent(this@RunningActivity, RunningService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        delay(1000L)
    }

    // 달리기 종료
    private fun stopRun() {
        unbindService(serviceConnection)
        sendCommandToService(ACTION_STOP_SERVICE)
    }


    private fun drawPolyline() {
        polyline.coords = naverLatLng
        polyline.color = getColor(R.color.mainColor)
        polyline.map = naverMap
    }

    private fun changeTimeText(time: String) {
        binding.apply {
            tvTime.text = time

            if (type == GOAL_TYPE_TIME) {
                /** goal amount 변경 **/
            }
        }
    }

    private fun changeDistanceText(sumDistance: Float) {
        binding.apply {
            tvDistance.text = TrackingUtility.getFormattedDistance(sumDistance)

            if (type == GOAL_TYPE_DISTANCE) {
                /** goal amount 변경 **/
            }
        }
    }

    private fun changeCalorie(sumDistance: Float) {
        caloriesBurned = round((sumDistance / 1000f) * weight).toInt()
        binding.tvCalorie.text = "$caloriesBurned"
    }


    // 서비스에게 명령을 전달함
    private fun sendCommandToService(action: String) {
        Intent(this, RunningService::class.java).also {
            it.action = action
            this.startService(it)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val bind = service as RunningService.LocalBinder
            runningService = bind.getService()
            initObserve()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 라이트 모드 설정 시 지도 심벌의 클릭 이벤트를 처리할 수 없습니다
        this.naverMap = naverMap
        naverMap.moveCamera(CameraUpdate.zoomTo(16.0))
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.isLiteModeEnabled = true
        // 현위치 버튼 활성화
        naverMap.uiSettings.isLocationButtonEnabled = true

    }
}