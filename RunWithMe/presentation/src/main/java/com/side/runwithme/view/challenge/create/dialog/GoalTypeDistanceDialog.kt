package com.side.runwithme.view.challenge.create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogOneNumberpickerBinding

class GoalTypeDistanceDialog(private val listener: GoalTypeDistanceDialogListener): BaseDialogFragment<DialogOneNumberpickerBinding>(
    R.layout.dialog_one_numberpicker) {

    override fun init() {

        initNumberPicker()

        initClickListener()
    }

    private fun initNumberPicker(){
        binding.apply {
            np.minValue = 1
            np.maxValue = 60
            np.value = 3

            //순환 안되게 막기
            np.wrapSelectorWheel = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                np.textColor = ContextCompat.getColor(requireActivity(), R.color.main_blue)
            }

            np.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

    private fun initClickListener() {
        binding.apply {
            tvPositive.setOnClickListener {
                val amount = np.value
                listener.onItemClick(amount)
                dismiss()
            }
        }
    }
}