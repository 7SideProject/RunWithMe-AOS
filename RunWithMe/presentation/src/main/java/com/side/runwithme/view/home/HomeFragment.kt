package com.side.runwithme.view.home


import android.accounts.OnAccountsUpdateListener
import android.app.UiAutomation.OnAccessibilityEventListener
import android.view.MenuItem.OnActionExpandListener
import com.example.seobaseview.base.BaseFragment
import androidx.navigation.fragment.findNavController
import com.naver.maps.map.OnMapReadyCallback
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home), OnMapReadyCallback, OnAccessibilityEventListener, OnAccountsUpdateListener, OnActionExpandListener {

    override fun init() {

        initClickListener()

    }

    private fun initClickListener(){
        binding.apply {

        }
    }


}
