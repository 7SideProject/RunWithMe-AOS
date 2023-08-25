package com.side.runwithme.view.challenge.create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogOneNumberpickerBinding

class CostDialog(private val listener: CostDialogListener): BaseDialogFragment<DialogOneNumberpickerBinding>(
    R.layout.dialog_one_numberpicker) {

    private lateinit var costValues : Array<String>

    override fun init() {

        initCostValues()

        initNumberPicker()

        initClickListener()
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

    private fun initClickListener() {
        binding.apply {
            tvPositive.setOnClickListener {
                val cost = costValues[np.value]
                listener.onItemClick(cost)
                dismiss()
            }
        }
    }
}