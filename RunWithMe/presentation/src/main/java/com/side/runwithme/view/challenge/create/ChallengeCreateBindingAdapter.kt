package com.side.runwithme.view.challenge.create

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.side.runwithme.util.GOAL_TYPE

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

@BindingAdapter("goalType")
fun TextView.setGoalType(goalType: String){
    if(goalType == GOAL_TYPE.TIME.type){
        this.text = "분"
    }else{
        this.text = "km"
    }
}

@BindingAdapter("isVisibleChallengeCreateButton")
fun AppCompatButton.setVisibleChallengeCreateButton(password: String?){
    if(password.isNullOrBlank()){
        this.visibility = View.GONE
    }else{
        this.visibility = View.VISIBLE
    }
}