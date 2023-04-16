package com.side.runwithme.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seobaseview.util.SingleLiveEvent
import com.side.domain.usecase.user.LoginUseCase
import com.side.domain.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    val joinEvent = SingleLiveEvent<String>()

    fun login(code: String, state: String){
        viewModelScope.launch(Dispatchers.IO){
            loginUseCase(code, state).collectLatest {

                if(it is ResultType.Success){
                    // 로그인 성공 -> Home으로 보내기
                    // 회원가입
                    joinEvent.call()
                }
                if(it is ResultType.Fail){

                }
                if(it is ResultType.Error){
                    Log.d("eettt", "login: ${it.exception}")
                }
            }
        }
    }


}
