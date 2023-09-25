package com.side.runwithme.view.challenge.create

import android.app.DatePickerDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeCreate2Binding
import com.side.runwithme.view.challenge.create.dialog.GoalWeeksDialog
import java.util.Calendar

class ChallengeCreateStep2Fragment : BaseFragment<FragmentChallengeCreate2Binding>(R.layout.fragment_challenge_create2) {

    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()


    override fun init() {

        binding.apply {
            challengeCreateVM = challengeCreateViewModel
        }

        initViewModelCallbacks()

        initClickListener()

    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

            btnChallengeDuration.setOnClickListener {
                initGoalWeeksDialog()
            }

            btnChallengeStartDate.setOnClickListener {
                initDatePickerDialog()
            }

            btnNext.setOnClickListener {
                findNavController().navigate(ChallengeCreateStep2FragmentDirections.actionChallengeCreateStep2FragmentToChallengeCreateStep3Fragment())
            }
        }
    }

    private fun initViewModelCallbacks(){
        challengeCreateViewModel.apply {
            initDate()
        }
    }

    private fun initGoalWeeksDialog() {
        val goalWeeksDialog = GoalWeeksDialog()
        goalWeeksDialog.show(childFragmentManager, "GoalWeeksDialog")
    }


    private fun initDatePickerDialog() {
        val today_calendar = Calendar.getInstance()
        val minDate = Calendar.getInstance()
        val maxDate = Calendar.getInstance()

        val dataSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            challengeCreateViewModel.setDateStart(year, month + 1, day)
        }

        val datePickerDialog: DatePickerDialog =
            DatePickerDialog(
                requireContext(),
                dataSetListener,
                today_calendar.get(Calendar.YEAR),
                today_calendar.get(Calendar.MONTH),
                today_calendar.get(Calendar.DAY_OF_MONTH)
            )

        minDate.time = today_calendar.time
        minDate.add(Calendar.DAY_OF_MONTH, 1)
        maxDate.time = today_calendar.time
        maxDate.add(Calendar.MONTH, 6)

        datePickerDialog.datePicker.minDate = minDate.time.time
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()
    }

}