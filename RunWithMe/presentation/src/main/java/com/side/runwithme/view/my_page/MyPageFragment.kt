package com.side.runwithme.view.my_page

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentMyPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {

    private val myPageViewModel by activityViewModels<MyPageViewModel>()

    override fun init() {
        binding.apply {
            myPageVM = myPageViewModel
        }

        initClickListener()
    }

    private fun initClickListener(){
        binding.apply {
            ivEditProfile.setOnClickListener {
                findNavController().navigate(MyPageFragmentDirections.actionMyPageFragmentToEditProfileFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        myPageViewModel.myPageInitRequest()
    }
}