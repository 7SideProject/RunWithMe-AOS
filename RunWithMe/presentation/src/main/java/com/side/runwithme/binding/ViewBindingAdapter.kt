package com.side.runwithme.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.side.runwithme.R
import com.side.runwithme.model.ChallengeParcelable
import com.side.runwithme.util.BASE_URL
import com.side.runwithme.util.costFormatter
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
fun TextView.setRunningTerm(challenge: ChallengeParcelable?){
    this.text = "${challenge!!.dateStart} ~ ${challenge.dateEnd}"
}

@BindingAdapter("runningDistance")
fun TextView.setRunnignDistance(distance: Int) {
    this.text = "${round((1F * distance / 1000) / 100) / 100} km"
}

@BindingAdapter("runningTime")
fun TextView.setRunningTime(time: Int) {
    val hourInt = time / 3600
    val minuteInt = (time / 60) % 60

    var hour = hourInt.toString()
    if (hourInt < 10) {
        hour = "0" + hour
    }

    var minute = minuteInt.toString()
    if (minuteInt < 10) {
        minute = "0" + minute
    }

    this.text = "${hour} : ${minute}"
}

@BindingAdapter("runningAvgSpeed")
fun TextView.setRunningAvgSpeed(avgSpeed: Double) {
    if (avgSpeed < 1) {
        this.text = "0.0 km/h"
        return
    }
    this.text = "${round(avgSpeed * 10.0) / 10.0} km/h"
}

@BindingAdapter("runningStartTime", "runningEndTime")
fun TextView.setRunningStartToEndTime(startTime: String, endTime: String) {

    this.text = "${getTime(startTime)} ~ ${getTime(endTime)}"
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

@BindingAdapter("imageSeq")
fun ImageView.setImageBySeq(imgSeq: Long) {
    Glide.with(this.context).load(BASE_URL + "/image/${imgSeq}").fitCenter()
        .override(Target.SIZE_ORIGINAL, R.dimen.challenge_detail_img_height).into(this)
}
