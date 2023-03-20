package com.side.runwithme.view.running_list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.seobaseview.base.BaseActivity
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningListBinding
import com.side.runwithme.util.LOCATION_PERMISSION_REQUEST_CODE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RunningListActivity : BaseActivity<ActivityRunningListBinding>(R.layout.activity_running_list), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Loading 후 Map 보이기
        lifecycleScope.launch {
            delay(1100)
            binding.apply {
                // lottie 가리기
                if(lottieRunningList.visibility == View.VISIBLE){
                    lottieRunningList.cancelAnimation()
                    lottieRunningList.visibility = View.GONE
                }
                // Map 띄우기
                if(layoutMap.visibility == View.INVISIBLE){
                    binding.layoutMap.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun init() {

        initMapView()

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