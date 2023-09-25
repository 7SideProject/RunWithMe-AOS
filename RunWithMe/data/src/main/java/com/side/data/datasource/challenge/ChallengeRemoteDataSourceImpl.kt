package com.side.data.datasource.challenge

import android.util.Log
import com.google.gson.Gson
import com.side.data.api.ChallengeApi
import com.side.data.model.request.CreateChallengeRequest
import com.side.domain.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRemoteDataSourceImpl @Inject constructor(
    private val challengeApi: ChallengeApi
) : ChallengeRemoteDataSource {

    override fun createChallenge(
        challenge: CreateChallengeRequest,
        imgFile: MultipartBody.Part?
    ): Flow<BaseResponse<Any?>> = flow {

        val json = Gson().toJson(challenge)
        val challenge = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        emit(challengeApi.createChallenge(challenge, imgFile))
    }
}