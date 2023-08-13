package com.side.runwithme.view.join

import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.side.runwithme.R

@BindingAdapter("enabledSendButton")
fun AppCompatButton.setEnabledSendButton(phoneNumber: String){
    if(phoneNumber.length == 11){
        this.apply {
            isEnabled = true
            setTextAppearance(R.style.StrongSmallButton)
        }
    }else{
        this.apply {
            isEnabled = false
            setTextAppearance(R.style.WeakSmallButton)
        }
    }
}

@BindingAdapter("enabledVerifyButton")
fun AppCompatButton.setEnabledVerifyButton(verifyNumber: String){
    if(verifyNumber.length == 6){
        this.apply {
            isEnabled = true
            setTextAppearance(R.style.StrongSmallButton)
        }
    }else{
        this.apply {
            isEnabled = false
            setTextAppearance(R.style.WeakSmallButton)
        }
    }
}

@BindingAdapter("enabledNextButton")
fun AppCompatButton.setEnabledNextButton(complete: Boolean){
    if(complete){
        this.apply {
            isEnabled = true
            setTextAppearance(R.style.StrongButton)
        }
    }else{
        this.apply {
            isEnabled = false
            setTextAppearance(R.style.WeakButton)
        }
    }
}

@BindingAdapter("enabledIDNextButton", "completePassword")
fun AppCompatButton.setEnabledEmailNextButton(id: String, completePassword: Boolean){

    if(id.length >= 4 && completePassword){
        this.apply {
            isEnabled = true
            setTextAppearance(R.style.StrongButton)
        }
    }else{
        this.apply {
            isEnabled = false
            setTextAppearance(R.style.WeakButton)
        }
    }
}



@BindingAdapter("enabledJoinButton")
fun AppCompatButton.setEnabledJoinButton(allDone: Boolean){
    if(allDone){
        this.apply {
            isEnabled = true
            setTextAppearance(R.style.StrongButton)
        }
    }else{
        this.apply {
            isEnabled = false
            setTextAppearance(R.style.WeakButton)
        }
    }
}