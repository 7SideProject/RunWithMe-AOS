package com.side.runwithme.view.challenge.create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogOneNumberpickerBinding

class GoalTypeTimeDialog(private val listener: GoalTypeTimeDialogListener): BaseDialogFragment<DialogOneNumberpickerBinding>(
    R.layout.dialog_one_numberpicker) {

    private lateinit var timeValues : Array<String>


    override fun init() {

        initTimeValues()

        initNumberPicker()

        initClickListener()
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

    private fun initClickListener() {
        binding.apply {
            tvPositive.setOnClickListener {
                val time = timeValues[np.value].toInt()
                listener.onItemClick(time)
                dismiss()
            }
        }
    }
}