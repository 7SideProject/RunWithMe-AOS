package com.side.runwithme.view.home

import android.graphics.Color
import android.provider.CalendarContract.Colors
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun init() {
        binding.toolbarMain.setBackButtonClickEvent {
            findNavController().navigate(R.id.action_homeFragment_to_crewListFragment)
        }


    }


}