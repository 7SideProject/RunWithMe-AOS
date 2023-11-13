package com.side.runwithme.view.challenge_detail

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.side.runwithme.R
import com.side.runwithme.util.CHALLENGE_STATE

@BindingAdapter("goalDays")
fun TextView.setGoalDays(goalDays: Int){
    this.text = "주 ${goalDays}회"
}

@BindingAdapter("challengeState")
fun AppCompatButton.setState(state: CHALLENGE_STATE){

    when(state){
        CHALLENGE_STATE.START -> {
            this.apply {
                text = resources.getString(R.string.running)
                visibility = View.VISIBLE
                setTextColor(ContextCompat.getColor(this.context, R.color.white))
                background = ContextCompat.getDrawable(this.context, R.drawable.btn_round_main_color)
            }
        }
        CHALLENGE_STATE.NOT_START_AND_ALEADY_JOIN -> {
            this.apply {
                text = resources.getString(R.string.challenge_quit)
                visibility = View.VISIBLE
                setTextColor(ContextCompat.getColor(this.context, R.color.black))
                background = ContextCompat.getDrawable(this.context, R.drawable.border_rectangle_blue)
            }
        }
        CHALLENGE_STATE.NOT_START_AND_NOT_JOIN -> {
            this.apply {
                text = resources.getString(R.string.challenge_join)
                visibility = View.VISIBLE
                setTextColor(ContextCompat.getColor(this.context, R.color.white))
                background = ContextCompat.getDrawable(this.context, R.drawable.btn_round_main_color)
            }
        }
        CHALLENGE_STATE.END -> {
            this.visibility = View.GONE
        }
        else -> {
            this.visibility = View.GONE
        }
    }
}
