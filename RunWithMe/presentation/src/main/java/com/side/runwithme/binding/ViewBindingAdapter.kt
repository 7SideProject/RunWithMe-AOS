package com.side.runwithme.binding

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.Target
import com.side.domain.model.Challenge
import com.side.runwithme.R
import com.side.runwithme.model.ChallengeParcelable
import com.side.runwithme.util.BASE_URL
import com.side.runwithme.util.CHALLENGE
import com.side.runwithme.util.GET_CHALLNEGE_IMG
import com.side.runwithme.util.GET_PROFILE_IMG
import com.side.runwithme.util.USER
import com.side.runwithme.util.costFormatter
import com.side.runwithme.view.challenge_detail.setChallengeImg
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Math.round


@BindingAdapter("runningResultDay")
fun TextView.setRunningResultDay(day: String) {
    if (day.isNullOrBlank()) {
        this.text = ""
        return
    }

    val date = day.split("-")
    val year = date.get(0)
    val month = date.get(1).toInt()
    val day = date.get(2).toInt()

    this.text = resources.getString(R.string.running_result_day, year, month, day)
}

@BindingAdapter("runningTerm")
fun TextView.setRunningTerm(challenge: ChallengeParcelable?) {
    this.text = "${challenge!!.dateStart} ~ ${challenge.dateEnd}"
}

@BindingAdapter("runningTermByChallenge")
fun TextView.setRunningTerm(challenge: Challenge?) {
    this.text = "${challenge!!.dateStart} ~ ${challenge.dateEnd}"
}

@BindingAdapter("runningDistance")
fun TextView.setRunnignDistance(distance: Int) {
    this.text = "${round((1F * distance / 1000) * 100) / 100} km"
}

@BindingAdapter("runningTime")
fun TextView.setRunningTime(time: Int) {
    val hourInt = time / 3600
    val minuteInt = (time / 60) % 60
    val secondInt = time % 60

    val text =
        if (hourInt != 0) "${hourInt}시 ${minuteInt}분 ${secondInt}초" else if (minuteInt == 0) "${secondInt}초" else "${minuteInt}분 ${minuteInt}초"

    this.text = text
}

@BindingAdapter("runningAvgSpeed")
fun TextView.setRunningAvgSpeed(avgSpeed: Double) {
    if (avgSpeed < 0.5) {
        this.text = "0.0 km/h"
        return
    }
    this.text = "${round(avgSpeed * 10.0) / 10.0} km/h"
}

@BindingAdapter("runningStartTime", "runningEndTime")
fun TextView.setRunningStartToEndTime(startTime: String, endTime: String) {

    this.text = "${startTime} ~ ${endTime}"
}

fun getTime(time: String): String {
    if (time.isBlank()) return ""

    val split = time.split(" ")
    val time = split.get(1).split(":")
    return "${time.get(0)}:${time.get(1)}"
}

@BindingAdapter("runningResultCompleted")
fun ImageView.setRunningResultCompleted(completed: String) {
    if (completed == "Y") {
        Glide.with(this.context)
            .load(R.drawable.success_stamp)
            .override(R.dimen.complete_stamp_width * 2, R.dimen.complete_stamp_height)
            .into(this)
    } else {
        Glide.with(this.context)
            .load(R.drawable.fail_stamp)
            .override(R.dimen.complete_stamp_width * 2, R.dimen.complete_stamp_height)
            .into(this)
    }
}

@BindingAdapter("runningCalorie")
fun TextView.setRunningCalorie(calorie: Int) {
    this.text = "$calorie kcal"
}

@BindingAdapter("costFormat")
fun AppCompatButton.setCostFormat(cost: String) {
    this.setText(costFormatter(cost))
}

@BindingAdapter("setHeight")
fun TextView.setHeight(height: Int) {
    this.text = "${height}cm"
}

@BindingAdapter("setWeight")
fun TextView.setWeight(weight: Int) {
    this.text = "${weight}kg"
}

@BindingAdapter("setPoint")
fun TextView.setPoint(point: Int) {
    this.text = "${point}P"
}

@BindingAdapter("setTotalRunningTime")
fun TextView.setTotalRunningTime(totalTime: Int) {
    val hour = totalTime / 3600
    val minute = (totalTime / 60) % 60
    this.text = "${hour}시간 ${minute}분"
}

@BindingAdapter("profileImg", "jwt")
fun CircleImageView.setProfileImg(userSeq: Long, jwt: String) {
    val glideUrl = GlideUrl(BASE_URL + USER + "/${userSeq}/" + GET_PROFILE_IMG) {
        mapOf(Pair("Authorization", jwt))
    }
    Glide.with(this.context).load(glideUrl).override(R.dimen.propfile_img_size).fitCenter()
        .placeholder(R.drawable.user_image).skipMemoryCache(true).into(this)
}