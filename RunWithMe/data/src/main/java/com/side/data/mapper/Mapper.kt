package com.side.data.mapper

import com.side.data.model.response.UserResponse
import com.side.domain.model.User

fun UserResponse.mapperToUser(): User = this.run {
        User(
            userName
        )
}