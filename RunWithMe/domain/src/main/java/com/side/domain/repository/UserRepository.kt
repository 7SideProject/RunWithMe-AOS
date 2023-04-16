package com.side.domain.repository

import com.side.domain.base.BaseResponse
import com.side.domain.model.User
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow

typealias UserResponse = ResultType<BaseResponse<String>>

interface UserRepository {
    fun login(code: String, state: String): Flow<UserResponse>
}