package com.side.runwithme.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun login(accessToken: String){
        viewModelScope.launch(Dispatchers.IO){
            loginUseCase.execute(accessToken).collectLatest {
                if(it is ResultType.Success){

                }
                if(it is ResultType.Fail){

                }
                if(it is ResultType.Error){

                }
            }
        }
    }
}