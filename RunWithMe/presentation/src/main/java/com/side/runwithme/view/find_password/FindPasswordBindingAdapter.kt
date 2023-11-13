package com.side.runwithme.view.find_password

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter

@BindingAdapter("findPasswordState")
fun AppCompatButton.setVisibleState(state: Boolean){
    if(state){
        this.visibility = View.VISIBLE
    }else{
        this.visibility = View.INVISIBLE
    }
}