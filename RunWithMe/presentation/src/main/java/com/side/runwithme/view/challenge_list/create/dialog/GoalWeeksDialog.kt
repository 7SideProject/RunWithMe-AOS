package com.side.runwithme.view.challenge_list.create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogGoalWeeksBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.challenge_list.create.ChallengeCreateViewModel

class GoalWeeksDialog: BaseDialogFragment<DialogGoalWeeksBinding>(R.layout.dialog_goal_weeks) {

    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()

    override fun init() {

        binding.apply {
            challengeCreateVM = challengeCreateViewModel
        }

        initNumberPicker()

        initViewModelCallbacks()

    }

    private fun initViewModelCallbacks(){
        repeatOnStarted {
            challengeCreateViewModel.dialogGoalWeeksEventFlow.collect {
                if(it is ChallengeCreateViewModel.Event.Success){
                    dismiss()
                }
            }
        }
    }

    private fun initNumberPicker(){
        binding.apply {
            np.minValue = 1
            np.maxValue = 25
            np.value = 4
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                np.textColor = ContextCompat.getColor(requireActivity(), R.color.main_blue)
            }

            //순환 안되게 막기
            np.wrapSelectorWheel = false

            np.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

}