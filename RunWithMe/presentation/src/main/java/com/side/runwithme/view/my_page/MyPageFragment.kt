package com.side.runwithme.view.my_page

import androidx.fragment.app.viewModels
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentMyPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {

    private val myPageViewModel by viewModels<MyPageViewModel>()

    override fun init() {
        binding.apply {
            myPageVM = myPageViewModel
        }

        myPageViewModel.getUserProfileRequest()
    }
}