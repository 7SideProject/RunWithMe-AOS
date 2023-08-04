package com.side.runwithme.view.running_list

import android.util.Log
import android.widget.ImageButton
import androidx.databinding.BindingAdapter

@BindingAdapter("btnTTS")
fun ImageButton.settingBtnTTS(option: Boolean) {
    Log.d("test123", "settingBtnTTS: $option")
    if(option){
        this.alpha = 1.0F
    }else{
        this.alpha = 0.5F
    }
}