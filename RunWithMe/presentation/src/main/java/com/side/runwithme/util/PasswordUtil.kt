package com.side.runwithme.util

fun passwordValidation(password: String, passwordConfirm: String): PasswordVerificationType{
    return if(password != passwordConfirm){
        PasswordVerificationType.NOT_EQUAL_ERROR
    }else if(password.length < 8 || password.length > 16){
        PasswordVerificationType.LENGTH_ERROR
    }else{
        PasswordVerificationType.SUCCESS
    }
}

enum class PasswordVerificationType{
    NOT_EQUAL_ERROR, LENGTH_ERROR, SUCCESS
}