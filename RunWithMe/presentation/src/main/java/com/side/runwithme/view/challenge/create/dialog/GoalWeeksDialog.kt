package com.side.runwithme.view.challenge.create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogOneNumberpickerBinding
import com.side.runwithme.databinding.DialogWeeksBinding

class GoalWeeksDialog(private val listener: GoalWeeksDialogListener): BaseDialogFragment<DialogOneNumberpickerBinding>(R.layout.dialog_one_numberpicker) {

    override fun init() {

        initNumberPicker()

        initClickListener()
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

    private fun initClickListener() {
        binding.apply {
            tvPositive.setOnClickListener {
                val weeks = np.value
                listener.onItemClick(weeks)
                dismiss()
            }
        }
    }
}