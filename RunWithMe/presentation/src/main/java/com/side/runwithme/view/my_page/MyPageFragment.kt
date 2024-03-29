package com.side.runwithme.view.my_page

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
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

        myPageViewModel.getJwtData()
    }

    private fun initClickListener(){
        binding.apply {
            ivEditProfile.setOnClickListener {
                findNavController().navigate(MyPageFragmentDirections.actionMyPageFragmentToEditProfileFragment())
            }
            toolbar.apply {
                setBackButtonClickEvent {
                    findNavController().popBackStack()
                }

                setOptionButtonClickEvent(1){
                    findNavController().navigate(MyPageFragmentDirections.actionMyPageFragmentToOthersFragment())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        myPageViewModel.myPageInitRequest()

    }

}