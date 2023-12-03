package com.side.runwithme.view.others

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.side.runwithme.R
import com.side.runwithme.base.BaseFragment
import com.side.runwithme.databinding.FragmentOthersBinding
import com.side.runwithme.util.repeatOnStarted
import com.side.runwithme.view.login.LoginActivity
import com.side.runwithme.view.others.dialog.DeleteUserDialog
import com.side.runwithme.view.others.dialog.DeleteUserDialogClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OthersFragment : BaseFragment<FragmentOthersBinding>(R.layout.fragment_others) {

    private val othersViewModel by viewModels<OthersViewModel>()

    companion object {
        val questionUri = Uri.parse("http://pf.kakao.com/_DxdWbG")
        val termsUri = Uri.parse("https://paper-fir-422.notion.site/970bb6b90ac945fa99b6368262dd0c3c?pvs=4")
    }

    override fun init() {


        initClickListener()

        initViewModelCallbacks()
    }

    private fun initViewModelCallbacks(){
        repeatOnStarted {
            othersViewModel.deleteUserEventFlow.collectLatest {
                when(it){
                    is OthersViewModel.DeleteUserEvent.Success -> {
                        showToast(resources.getString(R.string.success_delete_user))
                        moveLoginActivity()
                    }
                    else -> {
                        showToast(resources.getString(R.string.fail_server_error))
                    }
                }
            }
        }

        repeatOnStarted {
            othersViewModel.logoutEventFlow.collectLatest {
                if(it is OthersViewModel.LogoutEvent.Success){
                    showToast(resources.getString(R.string.success_logout))
                    moveLoginActivity()
                }
            }
        }
    }

    private fun moveLoginActivity(){
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

            tvDeleteUser.setOnClickListener {
                initDeleteUserDialog()
            }

            tvLogout.setOnClickListener {
                othersViewModel.logOut()
            }

            tvQuestion.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = questionUri
                }
                startActivity(intent)
            }

            tvTerms.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = termsUri
                }
                startActivity(intent)
            }
        }
    }

    private fun initDeleteUserDialog(){
        val deleteUserDialog = DeleteUserDialog(deleteUserDialogClickListener)
        deleteUserDialog.show(childFragmentManager, "DeleteUserDialog")
    }

    private val deleteUserDialogClickListener = object : DeleteUserDialogClickListener {
        override fun onClick(flag: Boolean) {
            if(flag){
                othersViewModel.deleteUser()
            }
        }
    }

}