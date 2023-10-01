package com.side.runwithme.view.running_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.seobaseview.base.BaseActivity
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityRunningListBinding
import com.side.runwithme.util.LOCATION_PERMISSION_REQUEST_CODE
import com.side.runwithme.view.running.RunningActivity
import com.side.runwithme.view.running_list.bottomsheet.RunningListBottomSheet
import com.side.runwithme.view.running_list.bottomsheet.practice.PracticeSettingClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RunningListActivity : BaseActivity<ActivityRunningListBinding>(R.layout.activity_running_list), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

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
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
//            btnTts.setOnClickListener {
//                runningListViewModel.clickTTsBtn()
//            }
        }
    }

    private val intentToRunningActivityClickListener = object : IntentToRunningActivityClickListener {
        override fun onItemClick(challengeSeq: Long) {
            /** 러닝 가능한 지 확인 **/
            val intent = Intent(this@RunningListActivity, RunningActivity::class.java)
            intent.putExtra("challengeSeq", challengeSeq)
            /** goalType, goalAmount 넘겨야함 **/
            startActivity(intent)
            finish()
        }
    }

    private val practiceSettingClickListener = object : PracticeSettingClickListener {
        override fun onItemClick(type: Int, amount: Int) {
            val intent = Intent(this@RunningListActivity, RunningActivity::class.java)
            intent.apply {
                putExtra("challengeSeq", -1)
                putExtra("goalType", type)
                putExtra("goalAmount", amount)
            }
            startActivity(intent)
            finish()
        }
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

        /** apply 적용 시 내 위치로 이동하지 않는 현상 **/
        naverMap.moveCamera(CameraUpdate.zoomTo(16.0))
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

    }
}