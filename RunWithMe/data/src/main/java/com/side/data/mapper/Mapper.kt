package com.side.data.mapper

import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.JoinResponse
import com.side.data.model.response.UserResponse
import com.side.domain.model.User

fun JoinResponse.mapperToUser(): User = this.run {
    User(seq, email, nickname, height, weight, point, profileImgSeq)
}

fun User.mapperToJoinRequest(): JoinRequest = this.run {
    JoinRequest(email, password, nickname, height, weight)
}

fun EmailLoginResponse.mapperToUser(): User = this.run {
    User(seq, email, nickname, height, weight, point, 0L)
}

fun User.mapperToEmailLoginRequest(): EmailLoginRequest = this.run {
    EmailLoginRequest(email, password)
}