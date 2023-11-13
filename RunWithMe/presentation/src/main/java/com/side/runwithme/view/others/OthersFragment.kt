package com.side.runwithme.view.others

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.seobaseview.base.BaseFragment
import com.side.runwithme.R
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
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    else -> {
                        showToast(resources.getString(R.string.fail_server_error))
                    }
                }
            }
        }
    }

    private fun initClickListener(){
        binding.apply {
            toolbar.setBackButtonClickEvent {
                findNavController().popBackStack()
            }

            tvDeleteUser.setOnClickListener {
                initDeleteUserDialog()
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