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
import com.side.runwithme.util.DRAWING_POLYLINE_FAST
import com.side.runwithme.util.LOCATION_PERMISSION_REQUEST_CODE
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
            layoutDistance.setOnClickListener {
                zoomToWholeTrack()
            }
        }
    }

    private fun initViewModelCallback(){
        repeatOnStarted {
            routeDetailViewModel.coordinates.collect {
                if(it.isNotEmpty()) {
                    zoomToWholeTrack()

                    // polyline.coords에 2개 이상의 리스트를 넣어야함
                    val naverLatLngs = mutableListOf<LatLng>()
                    naverLatLngs.add(it.get(0).mapperToNaverLatLng())

                    for (i in 1 until it.size) {
                        naverLatLngs.add(it.get(i).mapperToNaverLatLng())
                        drawPolyline(naverLatLngs)

                        val delayTime = if (it.size < 20) {
                            DRAWING_POLYLINE_FAST.SHORT
                        } else if (it.size < 50) {
                            DRAWING_POLYLINE_FAST.MIDDLE
                        } else {
                            DRAWING_POLYLINE_FAST.LONG
                        }

                        delay(delayTime.time)
                    }

                }
            }
        }
    }

    private fun zoomToWholeTrack(){
        val latlngs = routeDetailViewModel.coordinates.value.mapperToNaverLatLngList()

        if(latlngs.isNullOrEmpty() || naverMap == null) return

        val latLngBoundsBuilder = LatLngBounds.Builder().include(latlngs)
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
        val fm = childFragmentManager
        val mapFragment = fm!!.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm!!.beginTransaction().add(R.id.map_view, it).commit()
            }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        naverMap.locationSource = locationSource

        naverMap.isLiteModeEnabled = true

        initViewModelCallback()

    }

}