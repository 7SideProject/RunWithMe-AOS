package com.side.runwithme.view.challenge_board

import com.side.runwithme.R
import com.side.runwithme.base.BaseDialogFragment
import com.side.runwithme.databinding.DialogBoardConfirmBinding

class BoardConfirmDialog (
    private val content: String,
    private val boardSeq: Long,
    private val clickListener: BoardConfirmClickListener
): BaseDialogFragment<DialogBoardConfirmBinding>(R.layout.dialog_board_confirm){

    override fun init() {
        dialog?.window?.setBackgroundDrawableResource(R.color.white)

        initClickListener()

        binding.tvConfirmText.text = content

    }

    private fun initClickListener() {
        binding.apply {
            btnConfirm.setOnClickListener {
                clickListener.onClick(boardSeq)
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

}