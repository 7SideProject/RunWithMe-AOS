package com.side.runwithme.view.challenge.create

import android.app.DatePickerDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeCreate2Binding
import com.side.runwithme.view.challenge.create.dialog.GoalWeeksDialog
import com.side.runwithme.view.challenge.create.dialog.GoalWeeksDialogListener
import java.util.Calendar

class ChallengeCreate2Fragment : BaseFragment<FragmentChallengeCreate2Binding>(R.layout.fragment_challenge_create2) {

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
                findNavController().navigate(R.id.action_challengeCreate2Fragment_to_challengeCreate3Fragment)
            }
        }
    }

    private fun initViewModelCallbacks(){
        challengeCreateViewModel.apply {
            initDate()
        }
    }

    private fun initGoalWeeksDialog() {
        val goalWeeksDialog = GoalWeeksDialog(goalWeeksDialogListener)
        goalWeeksDialog.show(childFragmentManager, "GoalWeeksDialog")
    }

    private val goalWeeksDialogListener: GoalWeeksDialogListener = object : GoalWeeksDialogListener {
        override fun onItemClick(weeks: Int) {
            /** goalweeks 설정 후, 끝나는 날 변경되도록 **/
            challengeCreateViewModel.setWeeks(weeks)
        }
    }

    private fun initDatePickerDialog() {
        val today_calendar = Calendar.getInstance()
        val minDate = Calendar.getInstance()
        val maxDate = Calendar.getInstance()

//        val now = System.currentTimeMillis()
//        val date = Date(now)
//        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd")
//        val today = dateTimeFormat.format(date)
//        val todayCalendarType = dateTimeFormat.parse(today)
//
//        today_calendar.time = todayCalendarType

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