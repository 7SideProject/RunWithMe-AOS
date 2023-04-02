package com.side.runwithme.view.running_result

import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentRuuningResultBinding
import com.side.runwithme.util.LOCATION_PERMISSION_REQUEST_CODE
import com.side.runwithme.view.loading.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RuuningResultFragment : BaseFragment<FragmentRuuningResultBinding>(R.layout.fragment_ruuning_result),
    OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private lateinit var loadingDialog : LoadingDialog


    override fun init() {

        initMapView()

        initLoadingDialog()
    }

    private fun initLoadingDialog(){
        loadingDialog = LoadingDialog(requireContext())
        loading()
    }

    private fun loading(){
        loadingDialog.show()
        // 로딩이 진행되지 않았을 경우
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            if(loadingDialog.isShowing){
                loadingDialog.dismiss()
            }
        }
    }

    private fun initClickListener(){
        binding.apply {
            btnRecommend.setOnClickListener {
                findNavController().navigate(R.id.action_ruuningResultFragment_to_createRecommendFragment)
            }

            btnRoute.setOnClickListener {
                findNavController().navigate(R.id.action_ruuningResultFragment_to_routeDetailFragment)
            }

            btnOk.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun initMapView(){
        val fm = activity?.supportFragmentManager
        val mapFragment = fm!!.findFragmentById(R.id.map_view) as MapFragment?
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