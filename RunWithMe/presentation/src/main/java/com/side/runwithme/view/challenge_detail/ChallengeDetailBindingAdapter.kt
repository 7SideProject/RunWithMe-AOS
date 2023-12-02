package com.side.runwithme.view.challenge_detail

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.Target
import com.side.domain.usecase.datastore.GetJWTDataStoreUseCase
import com.side.runwithme.R
import com.side.runwithme.binding.setProfileImg
import com.side.runwithme.util.BASE_URL
import com.side.runwithme.util.CHALLENGE
import com.side.runwithme.util.CHALLENGE_STATE
import com.side.runwithme.util.GET_CHALLNEGE_IMG
import javax.inject.Inject

@BindingAdapter("goalDays")
fun TextView.setGoalDays(goalDays: Int){
    this.text = "주 ${goalDays}회"
}

@BindingAdapter("challengeState")
fun AppCompatButton.setState(state: CHALLENGE_STATE){

    when(state){
        CHALLENGE_STATE.START -> {
            this.apply {
                /** 챌린지 detail 화면에서 러닝은 일단 안되게 하고 배포 후에 추가 예정 **/
//                text = resources.getString(R.string.running)
//                visibility = View.VISIBLE
//                setTextColor(ContextCompat.getColor(this.context, R.color.white))
//                background = ContextCompat.getDrawable(this.context, R.drawable.btn_round_main_color)
                visibility = View.GONE
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
        CHALLENGE_STATE.NOT_START_AND_MANAGER -> {
            this.apply {
                text = resources.getString(R.string.challenge_break)
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
        CHALLENGE_STATE.NOTHING -> {
            this.visibility = View.GONE
        }
    }
}

@BindingAdapter("challenge_img", "jwt")
fun ImageView.setChallengeImg(challengeSeq: Long, jwt: String){
    if(challengeSeq == 0L){
        Glide.with(this.context).load(R.drawable.bg_lightgrey).into(this)
        return
    }

    val glideUrl = GlideUrl(BASE_URL + CHALLENGE + "/${challengeSeq}/" + GET_CHALLNEGE_IMG) {
        mapOf(Pair("Authorization", jwt))
    }
    Glide.with(this.context).load(glideUrl).override(Target.SIZE_ORIGINAL, R.dimen.challenge_detail_img_height).fitCenter().placeholder(R.drawable.bg_lightgrey).diskCacheStrategy(
        DiskCacheStrategy.RESOURCE).into(this)
}
