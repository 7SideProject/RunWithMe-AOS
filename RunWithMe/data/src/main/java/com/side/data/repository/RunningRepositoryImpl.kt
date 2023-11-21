package com.side.data.repository

import android.util.Log
import com.google.gson.Gson
import com.side.data.datasource.challenge.ChallengeRemoteDataSource
import com.side.data.datasource.running.RunningRemoteDataSource
import com.side.data.mapper.mapperToRunRecordRequest
import com.side.data.model.request.RunRecordRequest
import com.side.data.util.asResult
import com.side.data.util.emitResultTypeError
import com.side.data.util.emitResultTypeFail
import com.side.data.util.emitResultTypeLoading
import com.side.data.util.emitResultTypeSuccess
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import com.side.domain.repository.NullResponse
import com.side.domain.repository.PostRunRecordResponse
import com.side.domain.repository.RunningRepository
import com.side.domain.utils.ResultType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunningRepositoryImpl @Inject constructor(
    private val runningRemoteDataSource: RunningRemoteDataSource,
    private val challengeRemoteDataSource: ChallengeRemoteDataSource
): RunningRepository{

    override fun postRunRecord(
        challengeSeq: Long,
        runRecord: RunRecord,
        image: MultipartBody.Part
    ): Flow<NullResponse> = flow<NullResponse> {
        emitResultTypeLoading()

        val json = Gson().toJson(runRecord)
        val runRecordRequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        runningRemoteDataSource.postRunRecord(challengeSeq, runRecordRequestBody, image).collect {
            when (it.code) {
                200 -> {
                    emitResultTypeSuccess(it)
                }

                else -> {
                    emitResultTypeFail(it)
                }
            }
        }

    }.catch {
        emitResultTypeError(it)
    }

    override fun isAvailableRunningToday(challengeSeq: Long): Flow<NullResponse> = flow<NullResponse> {
        emitResultTypeLoading()

        challengeRemoteDataSource.getChallengeDetail(challengeSeq).collect {
            Log.d("test123", "isAvailableRunningToday: repoimpl : ${it.data.toString()}")
            if(it.code == 300){
                if(it.data.canRunning){
                    emitResultTypeSuccess(it.changeData(null))
                }else {
                    emitResultTypeFail(it.changeData(null))
                }
            }else{
                emitResultTypeFail(it.changeData(null))
            }
        }


    }.catch {
        emitResultTypeError(it)
    }
}