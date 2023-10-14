package com.side.data.mapper


import com.side.data.model.request.EmailLoginRequest
import com.side.data.model.request.JoinRequest
import com.side.data.model.request.RunRecordRequest
import com.side.data.model.response.EmailLoginResponse
import com.side.domain.model.AllRunRecord
import com.side.domain.model.User



fun AllRunRecord.mapperToRunRecordRequest(): RunRecordRequest = this.run {
    RunRecordRequest(
        runRecord = runRecord, coordinates = coordinates, imgFile = imgFile
    )

}