package com.side.runwithme.view.challenge.create

import android.annotation.SuppressLint
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
import com.side.runwithme.databinding.FragmentChallengeCreate5Binding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.challenge.create.dialog.PasswordDialog
import com.side.runwithme.view.challenge.create.dialog.PasswordDialogListener
import com.side.runwithme.view.join.JoinViewModel
import com.side.runwithme.view.loading.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChallengeCreate5Fragment : BaseFragment<FragmentChallengeCreate5Binding>(R.layout.fragment_challenge_create5) {

    private val challengeCreateViewModel by activityViewModels<ChallengeCreateViewModel>()
    private lateinit var loadingDialog: LoadingDialog

    override fun init() {

        binding.apply {

        }

        loadingDialog = LoadingDialog(requireContext())

        initClickListener()

        initRadioGroupCheck()

        initViewModelCallBack()

    }

    private fun initViewModelCallBack() {
        repeatOnStarted {
            challengeCreateViewModel.createEventFlow.collectLatest {
                handleEvent(it)
            }
        }

    }

    private fun initClickListener() {
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

            btnCreate.setOnClickListener {
                challengeCreateViewModel.create()
                loading()
            }

            btnPassword.setOnClickListener {
                initPasswdDialog()
            }

        }
    }

    private fun loading(){
        loadingDialog.show()
        // 로딩이 진행되지 않았을 경우
        lifecycleScope.launch {
            delay(1500)
            if(loadingDialog.isShowing){
                loadingDialog.dismiss()
            }
        }
    }

    private fun initRadioGroupCheck() {
        binding.apply {
            rgPassword.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{

                override fun onCheckedChanged(radioGroup: RadioGroup?, checkId: Int) {
                    when(checkId){
                        R.id.rb_no_password ->{
                            binding.apply {
                                layoutPasswordExist.visibility = View.GONE
                                challengeCreateViewModel.setPassword(null)
                            }
                        }

                        R.id.rb_password -> {
                            binding.apply {
                                layoutPasswordExist.visibility = View.VISIBLE

                            }
                        }
                    }
                }
            })

        }
    }

    private fun initPasswdDialog() {
        val passwdDialog = PasswordDialog(passwdDialogListener)
        passwdDialog.show(childFragmentManager, "PasswordDialog")
    }

    private val passwdDialogListener: PasswordDialogListener = object : PasswordDialogListener {
        override fun onItemClick(passwd: String) {
            challengeCreateViewModel.setPassword(passwd)
        }
    }

    private fun handleEvent(event: ChallengeCreateViewModel.Event) {
        when (event) {
            is ChallengeCreateViewModel.Event.Success -> {
                showToast(event.message)
                loadingDialog.dismiss()
                challengeCreateViewModel.refresh()
                findNavController().navigate(R.id.action_challengeCreate5Fragment_to_challengeListFragment)
            }
            is ChallengeCreateViewModel.Event.Fail -> {
                showToast(event.message)
            }
            is ChallengeCreateViewModel.Event.Error ->{
                showToast("서버 오류 입니다. 잠시 후에 다시 시도해주세요.")
            }
        }
    }

}