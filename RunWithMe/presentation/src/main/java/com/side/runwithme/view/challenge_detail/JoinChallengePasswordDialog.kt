package com.side.runwithme.view.challenge_detail

import com.side.runwithme.R
import com.side.runwithme.base.BaseDialogFragment
import com.side.runwithme.databinding.DialogJoinChallengePasswordBinding

class JoinChallengePasswordDialog(private val joinChallengePasswordDialogClickListener: JoinChallengePasswordDialogClickListener) :
    BaseDialogFragment<DialogJoinChallengePasswordBinding>(
        R.layout.dialog_join_challenge_password
    ) {

    override fun init() {
        dialog?.window?.setBackgroundDrawableResource(R.color.white)

        initClickListener()

    }

    private fun initClickListener() {
        binding.apply {
            tvPositive.setOnClickListener {
                joinChallengePasswordDialogClickListener.onClick(etPasswd.text.toString())
                dismiss()
            }
        }
    }
}