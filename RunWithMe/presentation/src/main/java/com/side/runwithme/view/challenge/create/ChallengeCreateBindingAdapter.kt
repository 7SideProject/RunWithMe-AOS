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
fun TextView.setGoalType(goalType: GOAL_TYPE){
    if(goalType == GOAL_TYPE.TIME){
        this.text = "분"
    }else{
        this.text = "km"
    }
}

@BindingAdapter("isVisibleChallengeCreateButton", "isPasswordChallenge")
fun AppCompatButton.setVisibleChallengeCreateButton(password: String?, isPasswordChallenge: Boolean){
    if(!isPasswordChallenge){
        this.visibility = View.VISIBLE
        return
    }

    if(password.isNullOrBlank()){
        this.visibility = View.GONE
    }else{
        this.visibility = View.VISIBLE
    }
}