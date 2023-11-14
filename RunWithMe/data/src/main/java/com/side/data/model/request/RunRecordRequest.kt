package com.side.data.model.request

import com.google.gson.annotations.SerializedName
import com.side.domain.model.Coordinate
import com.side.domain.model.RunRecord
import okhttp3.MultipartBody

data class RunRecordRequest(
    @SerializedName("runRecordPostDto") val runRecord: RunRecord
)
