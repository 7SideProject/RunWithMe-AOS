package com.side.runwithme.view.challenge_create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.side.runwithme.R
import com.side.runwithme.base.BaseDialogFragment
import com.side.runwithme.databinding.DialogGoalTypeDistanceBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.challenge_create.ChallengeCreateViewModel

class GoalTypeDistanceDialog: BaseDialogFragment<DialogGoalTypeDistanceBinding>(
    R.layout.dialog_goal_type_distance) {

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
            challengeCreateViewModel.dialogGoalDistanceEventFlow.collect {
                if(it is ChallengeCreateViewModel.Event.Success){
                    dismiss()
                }
            }
        }
    }

    private fun initNumberPicker(){
        binding.apply {
            np.minValue = 1
            np.maxValue = 40
            np.value = 3

            //순환 안되게 막기
            np.wrapSelectorWheel = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                np.textColor = ContextCompat.getColor(requireActivity(), R.color.main_blue)
            }

            np.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }


}