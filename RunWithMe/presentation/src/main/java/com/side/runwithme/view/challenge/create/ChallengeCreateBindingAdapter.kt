package com.side.runwithme.view.challenge.create

import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.side.runwithme.R
import com.side.runwithme.view.join.setEnabledSendButton

@BindingAdapter("create1NextBtnAvailable")
fun AppCompatButton.setCreate1NextBtnAvailable(challengeName: String){
    if(challengeName.isBlank()){
        this.apply {
            visibility = View.GONE
        }
    }else{
        this.apply {
            visibility = View.VISIBLE

        }
    }

}