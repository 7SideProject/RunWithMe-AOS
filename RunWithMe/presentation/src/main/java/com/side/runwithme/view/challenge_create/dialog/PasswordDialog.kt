package com.side.runwithme.view.challenge_create.dialog

import androidx.fragment.app.activityViewModels
import com.example.seobaseview.base.BaseDialogFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.DialogPasswordBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.challenge_create.ChallengeCreateViewModel

class PasswordDialog(): BaseDialogFragment<DialogPasswordBinding>(
    R.layout.dialog_password) {

    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()

    override fun init() {

        binding.apply {
            challengeCreateVM = challengeCreateViewModel
        }

        initViewModelCallbacks()
    }

    private fun initViewModelCallbacks(){
        repeatOnStarted {
            challengeCreateViewModel.dialogPasswordEventFlow.collect {
                when(it) {
                    is ChallengeCreateViewModel.Event.Success -> {
                        dismiss()
                    }
                    is ChallengeCreateViewModel.Event.Fail -> {
                        showToast(it.message)
                        dismiss()
                    }
                    else -> {}
                }
            }
        }
    }
}