package com.side.runwithme.view.join

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentJoin3Binding
import com.side.runwithme.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest

class Join3Fragment: BaseFragment<FragmentJoin3Binding>(R.layout.fragment_join3) {

    private val joinViewModel by activityViewModels<JoinViewModel>()


    override fun init() {
        binding.apply {
            joinVM = joinViewModel
        }

        initClickListener()
        initSpinner()
        initViewModelCallBack()

    }

    private fun initClickListener() {
        binding.apply {
            btnJoin.setOnClickListener {
                joinViewModel.join()
            }
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

        }
    }

    private fun hideKeyboard(editText: EditText){
        val manager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(editText.applicationWindowToken, 0)
    }

    private fun showKeyboard(editText: EditText){
        val manager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInput(editText.rootView, InputMethodManager.SHOW_IMPLICIT)
        editText.requestFocus()
    }



    private fun initSpinner(){
        initWeightSpinner()
        initHeightSpinner()
    }

    private fun initWeightSpinner(){
        val weightList = Array(231) { i -> i + 20 }

        binding.spinnerEditWeight.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, weightList)
            setSelection(30)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setWeight(weightList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setWeight(weightList[position])
                }
            }
        }
    }
    private fun initHeightSpinner(){
        val heightList = Array(131) { i -> i + 120 }

        binding.spinnerEditHeight.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, heightList)
            setSelection(30)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setHeight(heightList[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    joinViewModel.setHeight(heightList[position])
                }
            }
        }
    }

    private fun initViewModelCallBack() {
        repeatOnStarted {
            joinViewModel.joinEventFlow.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(joinEvent: JoinViewModel.JoinEvent) {
        when (joinEvent) {
            is JoinViewModel.JoinEvent.Success -> {
                showToast(resources.getString(joinEvent.message))
                requireActivity().finish()
            }
            is JoinViewModel.JoinEvent.Fail -> {
                showToast(resources.getString(joinEvent.message))
            }
        }
    }
}