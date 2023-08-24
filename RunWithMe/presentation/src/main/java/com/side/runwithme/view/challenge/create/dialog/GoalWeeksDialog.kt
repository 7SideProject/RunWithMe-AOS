package com.side.runwithme.view.challenge.create.dialog

import android.widget.NumberPicker
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogWeeksBinding

class GoalWeeksDialog(private val listener: GoalWeeksDialogListener): BaseDialogFragment<DialogWeeksBinding>(R.layout.dialog_weeks) {

    override fun init() {

        initNumberPicker()

        initClickListener()
    }

    private fun initNumberPicker(){
        binding.apply {
            npWeeks.minValue = 1
            npWeeks.maxValue = 25

            //순환 안되게 막기
            npWeeks.wrapSelectorWheel = false

            npWeeks.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

    private fun initClickListener() {
        binding.apply {
            tvPositive.setOnClickListener {
                val amount = npWeeks.value
                listener.onItemClick(amount)
                dismiss()
            }
        }
    }
}