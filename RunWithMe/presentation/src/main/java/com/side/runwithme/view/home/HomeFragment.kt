package com.side.runwithme.view.home

import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun init() {

        initClickListener()

    }

    private fun initClickListener(){
        binding.apply {
            cvChallenge.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_challengeListFragment)
            }
        }

    }


}
