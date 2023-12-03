package com.side.runwithme.view.challenge_detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.side.runwithme.R
import com.side.runwithme.base.BaseDialogFragment
import com.side.runwithme.databinding.DialogChallengeDetailConfirmBinding

class ChallengeDetailConfirmDialog(
    private val content: String,
    private val clickListener: ChellengeDetailConfirmDialogClickListener
) : BaseDialogFragment<DialogChallengeDetailConfirmBinding>(R.layout.dialog_challenge_detail_confirm) {
    override fun init() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        initClickListener()

        binding.tvConfirmText.text = content

    }

    private fun initClickListener() {
        binding.apply {
            btnConfirm.setOnClickListener {
                clickListener.onClick()
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }
}

