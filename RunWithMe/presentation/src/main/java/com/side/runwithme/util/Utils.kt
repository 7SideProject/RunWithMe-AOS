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

fun isEmailValid(email: String): Boolean {
    val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}")
    return emailRegex.matches(email)
}