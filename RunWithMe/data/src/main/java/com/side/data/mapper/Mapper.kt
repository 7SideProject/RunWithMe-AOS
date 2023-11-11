package com.side.data.mapper


import com.side.data.model.request.RunRecordRequest
import com.side.domain.model.AllRunRecord


fun AllRunRecord.mapperToRunRecordRequest(): RunRecordRequest = this.run {
    RunRecordRequest(
        runRecord = runRecord
    )

}