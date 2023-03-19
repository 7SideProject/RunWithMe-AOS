package com.side.runwithme.view.crew

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.domain.utils.ResultType
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentCrewListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CrewListFragment : BaseFragment<FragmentCrewListBinding>(R.layout.fragment_crew_list) {

    private val crewViewModel: CrewViewModel by viewModels()

    private lateinit var crewListAdapter: CrewListAdapter

    override fun init() {
        binding.lifecycleOwner = this
        binding.crewVM = crewViewModel
        initToolbarClickListener()
        initCrewList()

    }

    private fun initToolbarClickListener() {
        binding.apply {
            toolbarCrewList.setBackButtonClickEvent {
                findNavController().popBackStack()
            }
            toolbarCrewList.setOptionButtonClickEvent {
                findNavController().navigate(R.id.action_crewListFragment_to_crewSearchFragment)
            }
        }

    }

    private fun initCrewList() {
        crewViewModel.getCrews()
        crewListAdapter = CrewListAdapter()
        binding.rvCrewList.adapter = crewListAdapter
    }
}