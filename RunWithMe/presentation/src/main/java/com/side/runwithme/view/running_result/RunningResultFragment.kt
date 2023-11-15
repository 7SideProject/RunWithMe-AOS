package com.side.runwithme.view.running_result

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentRuuningResultBinding
import com.side.runwithme.view.MainActivity

class RunningResultFragment : BaseFragment<FragmentRuuningResultBinding>(R.layout.fragment_ruuning_result){
//    OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap


    private val runningResultViewModel: RunningResultViewModel by activityViewModels<RunningResultViewModel>()

    override fun init() {

//        initMapView()
        binding.apply {
            runningResultVM = runningResultViewModel
        }

        initClickListener()


    }



    private fun initClickListener(){
        binding.apply {
//            btnRecommend.setOnClickListener {
//                findNavController().navigate(R.id.action_ruuningResultFragment_to_createRecommendFragment)
//            }

            btnRoute.setOnClickListener {
                runningResultViewModel.run {
                    val action = RunningResultFragmentDirections.actionRuuningResultFragmentToRouteDetailFragment(runRecord.value, coordinates.value)
                    findNavController().navigate(action)
                }
            }

            btnOk.setOnClickListener {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }

}