package com.side.runwithme.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.side.runwithme.R
import okhttp3.MultipartBody
import java.lang.Math.round


@BindingAdapter("runRecordImgFile")
fun ImageView.setRunRecordImgFile(imgFile: MultipartBody.Part) {
    Glide.with(this.context)
        .load(imgFile)
        .into(this)
}



@BindingAdapter("runningResultDay")
fun TextView.setRunningResultDay(day: String) {
    val date = day.split("-")
    val year = date.get(0)
    val month = date.get(1).toInt()
    val day = date.get(2).toInt()

    this.text = resources.getString(R.string.running_result_day, year, month, day)
}

@BindingAdapter("runningDistance")
fun TextView.setRunnignDistance(distance: Int) {
    this.text = "${distance / 1000} km"
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
    this.text = "${round(avgSpeed * 10.0) / 10.0} km/h"
}

@BindingAdapter("runningStartTime", "runningEndTime")
fun TextView.setRunningStartToEndTime(startTime: String, endTime: String) {
    this.text = "$startTime ~ $endTime"
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
