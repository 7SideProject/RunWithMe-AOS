package com.side.data.model.response

import com.google.gson.annotations.SerializedName
import com.side.domain.model.TotalRecord

data class TotalRecordResponse(
    val userSeq: Long,
    @SerializedName("userTotalRecord") val userTotalRecord: TotalRecord
)
