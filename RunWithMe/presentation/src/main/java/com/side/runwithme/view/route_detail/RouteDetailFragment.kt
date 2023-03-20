package com.side.runwithme.view.route_detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentRouteDetailBinding

class RouteDetailFragment : BaseFragment<FragmentRouteDetailBinding>(R.layout.fragment_route_detail) {

    override fun init() {

    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

        }
    }

}