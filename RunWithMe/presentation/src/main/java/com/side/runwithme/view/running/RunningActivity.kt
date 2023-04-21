package com.side.runwithme.view.running

import android.content.Intent
import android.location.Location
import android.view.View
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RunningActivity : BaseActivity<ActivityRunningBinding>(R.layout.activity_running),
    OnMapReadyCallback {

    // 라이브 데이터를 받아온 값들
    private var pathPoints = mutableListOf<PolyLine>()

    private lateinit var polyline: PathOverlay

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private var naverLatLng = mutableListOf<LatLng>()

    private lateinit var latLngBoundsBuilder: LatLngBounds.Builder

    override fun init() {
        initLatLngBounds()

        initMapView()

        initClickListener()

        initObserve()

        firstStart()

        initDrawLine()
    }

    private fun initLatLngBounds() {
        latLngBoundsBuilder = LatLngBounds.Builder()
    }

    private fun initClickListener(){
        binding.apply {
            ibStart.setOnClickListener {
                ibStart.visibility = View.GONE
                ibPause.visibility = View.VISIBLE

                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            }

            ibPause.setOnClickListener{
                ibStart.visibility = View.VISIBLE
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

    private fun initMapView(){
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

    private fun initObserve(){

        RunningService.pathPoints.observe(this){
            if(it.isNotEmpty()){

                val lat = it.last().latitude
                val lng = it.last().longitude
                val latlng = LatLng(lat, lng)

                naverLatLng.add(latlng)

                latLngBoundsBuilder.include(latlng)

                if(it.size >= 2){
                    drawPolyline()
                }

                // 경로 최적화
                if(it.size >= 4){
                    optimizationPolyLine()
                }
            }
        }

        // 시간(타이머) 경과 관찰
        RunningService.timeRunInMillis.observe(this){
            val formattedTime = TrackingUtility.getFormattedStopWatchTimeSummery(it)
            changeTimeText(formattedTime)
        }

        // 거리
        RunningService.sumDistance.observe(this){
            changeDistanceText(it)
        }

        val initSumDistance = 0F
        changeDistanceText(initSumDistance)
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
        if(lastDistance < 10){
            naverLatLng.removeAt(naverLatLng.size - 2)
            return
        }

        // 100미터 이상으로 너무 먼 점이면 각도 상관없이 무조건 저장
        if(lastDistance >= 100) return

        // 적당한 거리라면 각도 비교
        val v1: Vector = getVector(first, second) // 기존 벡터
        val v2: Vector = getVector(second, third) // 비교할 벡터

        val cosine = getVectorDotProduct(v1, v2) / (getVectorDistance(v1) * getVectorDistance(v2))

        // 거리가 가까울 땐 +- 5도 (거의 직선인 경우 삭제)
        if(lastDistance < 50){
            if(cosine > Math.cos(deg2rad(20.0))){
                naverLatLng.removeAt(naverLatLng.size - 2)
                return
            }
        }else{ // 거리가 멀어지면 여유범위 줄이기
            if(cosine > Math.cos(deg2rad(10.0))){
                naverLatLng.removeAt(naverLatLng.size - 2)
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

        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun firstStart() {
        if(RunningService.isFirstRun){

            lifecycleScope.launch(Dispatchers.Main) {
                sendCommandToService(ACTION_SHOW_RUNNING_ACTIVITY)
                delay(3000L)
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                delay(1000L)

            }
        }
    }

    // 달리기 종료
    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
    }


    private fun drawPolyline() {
        polyline.coords = naverLatLng
        polyline.color = getColor(R.color.mainColor)
        polyline.map = naverMap
    }

    private fun changeTimeText(time: String){
        binding.apply {
            tvTime.text = time
        }
    }

    private fun changeDistanceText(sumDistance : Float){
        binding.apply {
            tvDistance.text = TrackingUtility.getFormattedDistance(sumDistance)
        }
    }


    // 서비스에게 명령을 전달함
    private fun sendCommandToService(action : String) {
        Intent(this, RunningService::class.java).also {
            it.action = action
            this.startService(it)
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