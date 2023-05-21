package com.side.data.mapper

import com.side.data.model.request.JoinRequest
import com.side.data.model.response.JoinResponse
import com.side.data.model.response.UserResponse
import com.side.domain.model.User

fun JoinResponse.mapperToUser(): User = this.run {
    User(seq, email, nickname, height, weight, point, profileImgSeq)
}

fun User.mapperToJoinRequest(): JoinRequest = this.run {
    JoinRequest(email, password, nickname, height, weight)
}