package com.side.runwithme.view.challenge_create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.side.runwithme.R
import com.side.runwithme.base.BaseDialogFragment
import com.side.runwithme.databinding.DialogCostBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.challenge_create.ChallengeCreateViewModel

class CostDialog(): BaseDialogFragment<DialogCostBinding>(
    R.layout.dialog_cost) {

    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()

    private lateinit var costValues : Array<String>

    override fun init() {

        binding.apply {
            challengeCreateVM = challengeCreateViewModel
        }

        initCostValues()

        initNumberPicker()

        initViewModelCallbacks()
    }

    private fun initViewModelCallbacks(){
        repeatOnStarted {
            challengeCreateViewModel.dialogCostEventFlow.collect {
                if(it is ChallengeCreateViewModel.Event.Success){
                    dismiss()
                }
            }
        }
    }

    private fun initCostValues(){
        costValues = Array<String>(20, {i -> ((i + 1) * 500).toString()})
    }

    private fun initNumberPicker(){
        binding.apply {
            np.minValue = 0
            np.maxValue = 19
            np.value = 1
            np.displayedValues = costValues

            np.wrapSelectorWheel = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                np.textColor = ContextCompat.getColor(requireActivity(), R.color.main_blue)
            }

            np.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

}