package com.side.runwithme.view.challenge.create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogOneNumberpickerBinding

class MaxMemberDialog(private val listener: MaxMemberDialogListener): BaseDialogFragment<DialogOneNumberpickerBinding>(
    R.layout.dialog_one_numberpicker) {

    override fun init() {

        initNumberPicker()

        initClickListener()
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

    private fun initClickListener() {
        binding.apply {
            tvPositive.setOnClickListener {
                val max = np.value
                listener.onItemClick(max)
                dismiss()
            }
        }
    }
}