package com.side.runwithme.view.others.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogDeleteUserBinding

class DeleteUserDialog(private val deleteUserDialogClickListener: DeleteUserDialogClickListener) :
    BaseDialogFragment<DialogDeleteUserBinding>(R.layout.dialog_delete_user) {

    override fun init() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))


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