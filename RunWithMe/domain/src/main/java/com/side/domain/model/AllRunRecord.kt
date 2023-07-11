package com.side.domain.model

import okhttp3.MultipartBody

data class AllRunRecord(
    val runRecord: RunRecord,
    val coordinates: List<Coordinate>,
    val imgFile: MultipartBody.Part?
) : java.io.Serializable