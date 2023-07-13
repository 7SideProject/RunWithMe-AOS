package com.side.runwithme.view.route_detail

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.seobaseview.base.BaseFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentRouteDetailBinding
import com.side.runwithme.mapper.mapperToCoordinate
import com.side.runwithme.mapper.mapperToNaverLatLng
import com.side.runwithme.mapper.mapperToNaverLatLngList
import com.side.runwithme.mapper.mapperToRunRecord
import com.side.runwithme.util.LOCATION_PERMISSION_REQUEST_CODE
import com.side.runwithme.util.POLYLINE_DRAW
import com.side.runwithme.util.repeatOnStarted
import kotlinx.coroutines.delay

class RouteDetailFragment : BaseFragment<FragmentRouteDetailBinding>(R.layout.fragment_route_detail), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var polyline: PathOverlay

    private val args by navArgs<RouteDetailFragmentArgs>()
    private val routeDetailViewModel by viewModels<RouteDetailViewModel>()

    override fun init() {

        initMapView()

        initClickListener()

        initPolyline()

        val runRecord = args.runRecord
        val coordinates = args.coordinates

        Log.d("test123", "coordinates init: ${coordinates?.toList().toString()}")

        routeDetailViewModel.apply {
            if(runRecord != null) {
                putRunRecord(runRecord.mapperToRunRecord())
            }

            if(!coordinates.isNullOrEmpty()){
                putCoordinates(coordinates.toList().mapperToCoordinate())
            }

        }

    }

    private fun initPolyline(){
        polyline = PathOverlay()
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

        }
    }

    private fun initViewModelCallback(){
        repeatOnStarted {
            routeDetailViewModel.coordinates.collect {
                if(it.isEmpty()){
                    return@collect
                }

                // polyline.coords에 2개 이상의 리스트를 넣어야함
                val naverLatLngs = mutableListOf<LatLng>()
                naverLatLngs.add(it.get(0).mapperToNaverLatLng())

                for(i in 1 until it.size){
                    naverLatLngs.add(it.get(i).mapperToNaverLatLng())
                    drawPolyline(naverLatLngs)

                    val delayTime = if(it.size < 20){
                        POLYLINE_DRAW.SHORT.time
                    }else if(it.size < 50){
                        POLYLINE_DRAW.MIDDLE.time
                    }else {
                        POLYLINE_DRAW.LONG.time
                    }

                    delay(delayTime)
                }

                zoomToWholeTrack()
            }
        }
    }

    private fun zoomToWholeTrack(){
        val latLngBoundsBuilder = LatLngBounds.Builder().include(routeDetailViewModel.coordinates.value.mapperToNaverLatLngList())
        val bounds = latLngBoundsBuilder.build()
        naverMap.moveCamera(CameraUpdate.fitBounds(bounds, 300))
    }

    private fun drawPolyline(naverLatLngs: List<LatLng>) {
        polyline.coords = naverLatLngs
        polyline.color = requireActivity().getColor(R.color.mainColor)
        polyline.map = naverMap
//        zoomToLatLng(naverLatLngs.last())
    }

    private fun zoomToLatLng(naverLatLng: LatLng){
        naverMap.moveCamera(CameraUpdate.scrollTo(naverLatLng))
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
        zoomToWholeTrack()
        initViewModelCallback()

        naverMap.locationSource = locationSource

        naverMap.isLiteModeEnabled = true

    }



}