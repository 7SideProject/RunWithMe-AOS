package com.side.runwithme.util

import java.text.SimpleDateFormat


// 서버 시간 포매터
fun timeFormatter(time: Long?): String {
    if(time == null){
        return ""
    }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    return dateFormat.format(time)
}