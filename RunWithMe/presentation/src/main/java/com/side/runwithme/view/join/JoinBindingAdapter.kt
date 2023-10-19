package com.side.runwithme.view.join

import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.side.runwithme.R
import java.util.regex.Pattern

const val emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
@BindingAdapter("enabledSendButton")
fun AppCompatButton.setEnabledSendButton(email: String){
    if(Pattern.matches(emailValidation,email)) {
        this.apply {
            isEnabled = true
            setTextAppearance(R.style.enableSmallNextButton)
        }
    }else{
        this.apply {
            isEnabled = false
            setTextAppearance(R.style.disableSmallNextButton)
        }
    }
}

@BindingAdapter("enabledVerifyButton")
fun AppCompatButton.setEnabledVerifyButton(verifyNumber: String){
    if(verifyNumber.length == 6){
        this.apply {
            isEnabled = true
            setTextAppearance(R.style.enableSmallNextButton)
        }
    }else{
        this.apply {
            isEnabled = false
            setTextAppearance(R.style.disableSmallNextButton)
        }
    }
}

@BindingAdapter("enabledJoinButton")
fun AppCompatButton.setEnabledJoinButton(allDone: Boolean){
    if(allDone){
        this.apply {
            isEnabled = true
            setTextAppearance(R.style.enableNextButton)
        }
    }else{
        this.apply {
            isEnabled = false
            setTextAppearance(R.style.disableNextButton)
        }
    }
}