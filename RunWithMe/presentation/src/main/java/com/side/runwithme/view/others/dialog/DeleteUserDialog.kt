package com.side.runwithme.view.others.dialog

import com.side.runwithme.R
import com.side.runwithme.base.BaseDialogFragment
import com.side.runwithme.databinding.DialogDeleteUserBinding

class DeleteUserDialog(private val deleteUserDialogClickListener: DeleteUserDialogClickListener) :
    BaseDialogFragment<DialogDeleteUserBinding>(R.layout.dialog_delete_user) {

    override fun init() {
        dialog?.window?.setBackgroundDrawableResource(R.color.white)


        initClickListener()
    }

    private fun initClickListener() {
        binding.apply {
            btnConfirm.setOnClickListener {
                deleteUserDialogClickListener.onClick(true)
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

}