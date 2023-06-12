package com.side.runwithme.view.running_list.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogRunningListBottomSheetBinding
import com.side.runwithme.view.running_list.IntentToRunningActivityClickListener
import com.side.runwithme.view.running_list.RunningListAdapter
import com.side.runwithme.view.running_list.RunningListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunningListBottomSheet(private val intentToRunningActivityClickListener: IntentToRunningActivityClickListener): BottomSheetDialogFragment() {

    private lateinit var binding: DialogRunningListBottomSheetBinding
    private val runningListViewModel: RunningListViewModel by activityViewModels<RunningListViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_running_list_bottom_sheet, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        init()
    }

    private fun init(){
        binding.apply {
            runningListVM = runningListViewModel
            rcvMyChallengeRunningList.adapter = RunningListAdapter(intentToRunningActivityClickListener)
        }

        initClickListener()

        initViewModelCallback()
    }

    private fun initClickListener(){
        binding.apply {
            layoutPractice.setOnClickListener {
                /** Practice Dilaog **/
            }
        }
    }

    private fun initViewModelCallback(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}