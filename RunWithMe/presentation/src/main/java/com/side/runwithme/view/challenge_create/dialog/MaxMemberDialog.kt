package com.side.runwithme.view.challenge_create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogMaxMemberBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.challenge_create.ChallengeCreateViewModel

class MaxMemberDialog(): BaseDialogFragment<DialogMaxMemberBinding>(
    R.layout.dialog_max_member) {

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
            challengeCreateViewModel.dialogMaxMemberEventFlow.collect {
                if(it is ChallengeCreateViewModel.Event.Success){
                    dismiss()
                }
            }
        }
    }

    private fun initNumberPicker(){
        binding.apply {
            np.minValue = 2
            np.maxValue = 20
            np.value = 7
            np.wrapSelectorWheel = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                np.textColor = ContextCompat.getColor(requireActivity(), R.color.main_blue)
            }

            np.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

}