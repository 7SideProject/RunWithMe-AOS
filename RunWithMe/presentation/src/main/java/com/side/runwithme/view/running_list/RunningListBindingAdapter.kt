package com.side.runwithme.view.running_list

import android.widget.ImageButton
import androidx.databinding.BindingAdapter

@BindingAdapter("btnTTS")
fun ImageButton.settingBtnTTS(option: Boolean) {
    if(option){
        this.alpha = 1.0F
    }else{
        this.alpha = 0.5F
    }
}