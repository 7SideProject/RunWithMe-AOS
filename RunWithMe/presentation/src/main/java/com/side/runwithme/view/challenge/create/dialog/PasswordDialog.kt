package com.side.runwithme.view.challenge.create.dialog

import android.os.Build
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogOneNumberpickerBinding
import com.side.runwithme.databinding.DialogPasswordBinding

class PasswordDialog(private val listener: PasswordDialogListener): BaseDialogFragment<DialogPasswordBinding>(
    R.layout.dialog_password) {

    override fun init() {

        initClickListener()

    }



    private fun initClickListener() {
        binding.apply {
            tvPositive.setOnClickListener {
                val passwd = etPasswd.text.toString()
                listener.onItemClick(passwd)
                dismiss()
            }
        }
    }
}