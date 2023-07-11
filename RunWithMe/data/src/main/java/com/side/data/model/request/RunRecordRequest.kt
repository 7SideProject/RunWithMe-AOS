package com.side.data.model.request

import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import okhttp3.MultipartBody

data class RunRecordRequest(
    val runRecord: RunRecord,
    val coordinates: List<Coordinate>,
    val imgFile: MultipartBody.Part?
)
