package com.side.runwithme.view.running_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentRunningListBinding
import com.side.runwithme.util.LOCATION_PERMISSION_REQUEST_CODE
import com.side.runwithme.view.running.RunningActivity
import com.side.runwithme.view.running_list.bottomsheet.RunningListBottomSheet
import com.side.runwithme.view.running_list.bottomsheet.practice.PracticeSettingClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RunningListFragment : BaseFragment<FragmentRunningListBinding>(R.layout.fragment_running_list), OnMapReadyCallback {

    private var locationSource: FusedLocationSource? = null
    private var naverMap: NaverMap? = null

    private val runningListViewModel: RunningListViewModel by viewModels<RunningListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initMapView()

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
        binding.apply {
            runningListVM = runningListViewModel
        }
        initClickListener()

        runningListViewModel.apply {
            getMyChallenges()
            getMyScrap()
        }
    }

    private fun initClickListener(){
        binding.apply {
            lottieStartBtn.setOnClickListener {
                val bottomSheet = RunningListBottomSheet(intentToRunningActivityClickListener, practiceSettingClickListener)
                bottomSheet.show(childFragmentManager, bottomSheet.tag)
            }

            ivBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private val intentToRunningActivityClickListener = object : IntentToRunningActivityClickListener {
        override fun onItemClick(challengeSeq: Long) {
            /** 러닝 가능한 지 확인 **/
            val intent = Intent(requireContext(), RunningActivity::class.java)
            intent.putExtra("challengeSeq", challengeSeq)
            /** goalType, goalAmount 넘겨야함 **/
            startActivity(intent)

            // home 화면으로 돌아가기
            findNavController().popBackStack()
        }
    }

    private val practiceSettingClickListener = object : PracticeSettingClickListener {
        override fun onItemClick(type: Int, amount: Int) {
            val intent = Intent(requireContext(), RunningActivity::class.java)
            intent.apply {
                putExtra("challengeSeq", -1)
                putExtra("goalType", type)
                putExtra("goalAmount", amount)
            }
            startActivity(intent)

            // home 화면으로 돌아가기
            findNavController().popBackStack()
        }
    }


    private fun initMapView(){
        val fm = childFragmentManager
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

        /** apply 적용 시 내 위치로 이동하지 않는 현상 **/
        naverMap.moveCamera(CameraUpdate.zoomTo(16.0))
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

    }

    override fun onDestroy() {
        super.onDestroy()

        // fragment가 destroy될 때 메모리에서 해제될테지만
        // 명시적으로 설정하여 확실하게 해제 해주기 위해
        naverMap = null
        locationSource = null
    }

}