package com.side.runwithme.view.running_result

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentRuuningResultBinding
import com.side.runwithme.view.MainActivity

class RunningResultFragment : BaseFragment<FragmentRuuningResultBinding>(R.layout.fragment_ruuning_result){

    private val runningResultViewModel: RunningResultViewModel by activityViewModels<RunningResultViewModel>()

    override fun init() {
        binding.apply {
            runningResultVM = runningResultViewModel
        }

        initClickListener()
    }

    private fun initClickListener(){
        binding.apply {
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