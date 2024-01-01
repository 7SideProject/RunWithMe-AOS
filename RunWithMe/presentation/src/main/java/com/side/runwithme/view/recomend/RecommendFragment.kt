package com.side.runwithme.view.recomend

import androidx.navigation.fragment.findNavController
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentRecommendBinding
import com.side.runwithme.util.LOCATION_PERMISSION_REQUEST_CODE

class RecommendFragment : BaseFragment<FragmentRecommendBinding>(R.layout.fragment_recommend), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap


    override fun init() {

        initMapView()

        initClickListener()
    }

    private fun initClickListener(){
        binding.apply {
            cvRecommendInfo.setOnClickListener {
                findNavController().navigate(RecommendFragmentDirections.actionRecommendFragmentToRecommendDetailFragment())
            }
        }
    }

    private fun initMapView(){
        val fm = activity?.supportFragmentManager
        val mapFragment = fm!!.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm!!.beginTransaction().add(R.id.map_view, it).commit()
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