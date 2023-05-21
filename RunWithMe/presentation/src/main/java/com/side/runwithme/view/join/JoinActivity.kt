package com.side.runwithme.view.join

import android.widget.Toast
import androidx.activity.viewModels
import com.example.seobaseview.base.BaseActivity
import com.side.domain.model.User
import com.side.runwithme.R
import com.side.runwithme.databinding.ActivityJoinBinding
import com.side.runwithme.util.repeatOnStarted
import kotlinx.coroutines.flow.collectLatest
import com.side.runwithme.view.join.JoinViewModel.Event
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinActivity : BaseActivity<ActivityJoinBinding>(R.layout.activity_join) {

    private val joinViewModel by viewModels<JoinViewModel>()
    override fun init() {
        initClickListener()
        initViewModelCallBack()
    }

    private fun initClickListener() {
        binding.apply {
            btnJoin.setOnClickListener {
                joinViewModel.join(
                    User(
                        seq = 0L,
                        email = "abcdefg@naver.com",
                        password = "12341234",
                        nickname = "닉네임1",
                        height = 100,
                        weight = 100,
                        point = 0,
                        profileImgSeq = 0L
                    )
                )
            }
        }
    }

    private fun initViewModelCallBack() {

        repeatOnStarted {
            joinViewModel.joinEventFlow.collectLatest { event ->
                handleEvent(event)

            }
        }

    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.Success -> {
                showToast(event.message)
                finish()
            }
            is Event.Fail -> {
                showToast(event.message)
            }
        }
    }

}