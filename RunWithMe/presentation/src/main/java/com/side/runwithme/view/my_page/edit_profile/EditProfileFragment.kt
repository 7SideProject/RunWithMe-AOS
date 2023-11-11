package com.side.runwithme.view.my_page.edit_profile

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentEditProfileBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.my_page.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(R.layout.fragment_edit_profile) {

    private val myPageViewModel by activityViewModels<MyPageViewModel>()

    override fun init() {
        binding.apply {
            myPageVm = myPageViewModel
        }

        initSpinner()
        initClickListener()
        initViewModelCallbacks()
    }

    private fun initSpinner(){
        initWeightSpinner()
        initHeightSpinner()
    }

    private fun initClickListener(){
        binding.apply {
            btnModify.setOnClickListener {
                myPageViewModel.editProfileRequest()
            }
        }
    }

    private fun initViewModelCallbacks(){
        repeatOnStarted {
            myPageViewModel.editEventFlow.collectLatest {
                when(it){
                    is MyPageViewModel.EditEvent.Success -> {
                        showToast(resources.getString(it.message))
                        findNavController().popBackStack()
                    }
                    is MyPageViewModel.EditEvent.Fail-> {
                        showToast(resources.getString(it.message))
                    }
                }
            }
        }
    }


    private fun initWeightSpinner(){
        val weightList = Array(231) { i -> i + 20 }

        binding.spinnerEditWeight.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, weightList)
            setSelection(myPageViewModel.userProfile.value.weight - 20)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myPageViewModel.setWeight(weightList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myPageViewModel.setWeight(weightList[position])
                }
            }
        }
    }
    private fun initHeightSpinner(){
        val heightList = Array(131) { i -> i + 120 }

        binding.spinnerEditHeight.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, heightList)
            setSelection(myPageViewModel.userProfile.value.height - 120)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myPageViewModel.setHeight(heightList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myPageViewModel.setHeight(heightList[position])
                }
            }
        }
    }
}