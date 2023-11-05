package com.side.data.mapper

import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.response.DailyCheckResponse
import com.side.data.model.response.DuplicateCheckResponse
import com.side.data.model.response.EmailLoginResponse
import com.side.data.model.response.TotalRecordResponse
import com.side.data.model.response.UserResponse
import com.side.domain.model.DailyCheck
import com.side.domain.model.DuplicateCheck
import com.side.domain.model.TotalRecord
import com.side.domain.model.User

fun User.mapperToJoinRequest(): JoinRequest = this.run {
    JoinRequest(email, password, nickname, height, weight)
}

fun EmailLoginResponse.mapperToUser(): User = this.run {
    User(seq, email, nickname, height, weight, point)
}

fun User.mapperToEmailLoginRequest(): EmailLoginRequest = this.run {
    EmailLoginRequest(email, password)
}

fun UserResponse.mapperToUser(): User = this.run {
    User(seq, email, nickname, height, weight, point)
}

fun DuplicateCheckResponse.mapperToDuplicateCheck(): DuplicateCheck = this.run {
    DuplicateCheck(isDuplicated)
}

fun DailyCheckResponse.mapperToDailyCheck(): DailyCheck = this.run {
    DailyCheck(isSuccess)
}

fun TotalRecordResponse.mapperToTotalRecord(): TotalRecord = this.run {
    userTotalRecord.run {
        TotalRecord(totalTime,totalDistance,totalCalorie, totalLongestTime, totalLongestDistance, totalAvgSpeed)
    }
}