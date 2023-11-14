package com.side.runwithme.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat


// 서버 시간 포매터
fun timeFormatter(time: Long?): String {
    if(time == null){
        return ""
    }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    return dateFormat.format(time)
}

fun onlyTimeFormatter(time: Long?): String {
    if(time == null){
        return ""
    }
    val dateFormat = SimpleDateFormat("HH:mm:ss")

    return dateFormat.format(time)
}

// Cost 포매터
fun costFormatter(cost: String): String {
    val formatter: NumberFormat = DecimalFormat("#,###")
    return formatter.format(cost.toInt())
}