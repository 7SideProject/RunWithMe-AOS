package com.side.runwithme.util


val passwordPattern = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,}".toRegex()

fun passwordValidation(password: String, passwordConfirm: String): PasswordVerificationType{
    return if(password != passwordConfirm){
        PasswordVerificationType.NOT_EQUAL_ERROR
    }else if(password.length < 8 || password.length > 16){
        PasswordVerificationType.LENGTH_ERROR
    }else if(passwordPattern.matches(password)){
        PasswordVerificationType.NOT_VALID
    }else {
        PasswordVerificationType.SUCCESS
    }
}

enum class PasswordVerificationType{
    NOT_EQUAL_ERROR, LENGTH_ERROR, NOT_VALID, SUCCESS
}