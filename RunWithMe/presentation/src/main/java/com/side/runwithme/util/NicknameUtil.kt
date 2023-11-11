package com.side.runwithme.util

//한글, 영문, 숫자로만 2자~8자까지 입력 가능

private val pattern = "^[a-zA-Z0-9가-힣]+$".toRegex()
fun matchesNickNameRule(nickName: String): Boolean{
    if(nickName.length in 2..8 && pattern.matches(nickName)){
        return true
    }

    return false
}