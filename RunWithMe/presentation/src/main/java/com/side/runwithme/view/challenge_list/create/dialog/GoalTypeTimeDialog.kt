package com.side.runwithme.view.challenge_list.create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogGoalTypeTimeBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.challenge_list.create.ChallengeCreateViewModel

class GoalTypeTimeDialog(): BaseDialogFragment<DialogGoalTypeTimeBinding>(
    R.layout.dialog_goal_type_time) {

    private lateinit var timeValues : Array<String>
    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()

    override fun init() {

        binding.apply {
            challengeCreateVM = challengeCreateViewModel
        }

        initTimeValues()

        initNumberPicker()

        initViewModelCallbacks()
    }

    private fun initViewModelCallbacks(){
        repeatOnStarted {
            challengeCreateViewModel.dialogGoalTimeEventFlow.collect {
                if(it is ChallengeCreateViewModel.Event.Success){
                    dismiss()
                }
            }
        }
    }

    private fun initTimeValues(){
        timeValues = Array<String>(60, {i -> ((i + 1) * 10).toString()})
    }

    private fun initNumberPicker(){
        binding.apply {
            np.minValue = 0
            np.maxValue = 59
            np.value = 3
            np.displayedValues = timeValues
            //순환 안되게 막기
            np.wrapSelectorWheel = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                np.textColor = ContextCompat.getColor(requireActivity(), R.color.main_blue)
            }

            np.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

}